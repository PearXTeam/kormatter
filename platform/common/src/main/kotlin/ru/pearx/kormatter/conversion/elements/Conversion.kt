package ru.pearx.kormatter.conversion.elements

import ru.pearx.kormatter.*
import ru.pearx.kormatter.conversion.PartDependency
import ru.pearx.kormatter.exceptions.IllegalFlagsException
import ru.pearx.kormatter.exceptions.IllegalPrecisionException
import ru.pearx.kormatter.exceptions.IllegalWidthException
import ru.pearx.kormatter.exceptions.NoSuchArgumentException
import ru.pearx.kormatter.utils.parser.FormatString
import ru.pearx.kormatter.utils.ArgumentIndexHolder


/*
 * Created by mrAppleXZ on 09.08.18.
 */
abstract class Conversion : IConversion
{
    override fun check(str: FormatString)
    {
        if(!widthDependency.check(str.width))
            throw IllegalWidthException(str, widthDependency)
        if(!precisionDependency.check(str.precision))
            throw IllegalPrecisionException(str, precisionDependency)
        checkFlags(str)
    }

    protected open fun checkFlags(str: FormatString)
    {
        for (flag in str.flags)
        {
            if (flag == FLAG_LEFT_JUSTIFIED && widthDependency != PartDependency.FORBIDDEN)
                continue
            if (flag == FLAG_REUSE_ARGUMENT_INDEX && canTakeArguments)
                continue
            throw IllegalFlagsException(str, "The conversion doesn't support the '$flag' flag.")
        }
    }

    protected open fun takeArgument(str: FormatString, indexHolder: ArgumentIndexHolder, vararg args: Any?): Any?
    {
        //use explicit argument index
        when
        {
            str.argumentIndex != null ->
            {
                try
                {
                    indexHolder.lastTaken = str.argumentIndex - 1
                    return args[indexHolder.lastTaken]
                }
                catch (e: IndexOutOfBoundsException)
                {
                    throw NoSuchArgumentException(str, "Can't use the argument at index ${indexHolder.lastTaken}!", e)
                }
            }
            //reuse previous argument index
            str.flags.contains(FLAG_REUSE_ARGUMENT_INDEX) ->
            {
                try
                {
                    return args[indexHolder.lastTaken]
                }
                catch (e: IndexOutOfBoundsException)
                {
                    throw NoSuchArgumentException(str, "Can't reuse previously taken argument (${indexHolder.lastTaken})!", e)
                }
            }
            else ->
            {
                //take the next argument
                indexHolder.lastOrdinary++
                indexHolder.lastTaken = indexHolder.lastOrdinary
                try
                {
                    return args[indexHolder.lastTaken]
                }
                catch (e: IndexOutOfBoundsException)
                {
                    throw NoSuchArgumentException(str, "Can't take the next ordinary argument (${indexHolder.lastTaken})!", e)
                }
            }
        }
    }
}