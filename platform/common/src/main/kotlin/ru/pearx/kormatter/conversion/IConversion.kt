/*
 * Copyright © 2018 mrAppleXZ
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ru.pearx.kormatter.conversion

import ru.pearx.kormatter.FLAG_LEFT_JUSTIFIED
import ru.pearx.kormatter.IllegalFlagsException
import ru.pearx.kormatter.IllegalPrecisionException
import ru.pearx.kormatter.IllegalWidthException
import ru.pearx.kormatter.parser.FormatString


/*
 * Created by mrAppleXZ on 04.08.18.
 */
interface IConversion
{
    val widthDependency: PartDependency

    val precisionDependency: PartDependency

    fun checkFlags(str: FormatString)
    {
        for(flag in str.flags)
        {
            if(flag == FLAG_LEFT_JUSTIFIED && widthDependency != PartDependency.FORBIDDEN)
                break
            throw IllegalFlagsException("'$str' doesn't support the '$flag' flag.")
        }
    }

    fun format(str: FormatString): String

    fun check(str: FormatString)
    {
        widthDependency.checkAndThrow(str, str.width, "width") { msg -> IllegalWidthException(msg) }
        precisionDependency.checkAndThrow(str, str.precision, "precision") { msg -> IllegalPrecisionException(msg) }
        checkFlags(str)
    }
}