package net.sf.colossus.util;


import java.util.ArrayList;
import java.util.List;
import edu.ucr.cs.riple.taint.ucrtainting.qual.RUntainted;


/**
 * Perl-style split function.
 *
 * Still useful, because String.split works with arrays and
 * regexes, not Lists and plain Strings.
 *
 * @see Glob
 *
 * @author David Ripton
 */

public final class Split
{

    /** Split the string into a list of substrings delimited by sep. */
    public static List<@RUntainted String> split(final @RUntainted char sep, final @RUntainted String s)
    {
        return split("" + sep, s);
    }

    public static @RUntainted List<@RUntainted String> split(final @RUntainted String sep, final @RUntainted String s)
    {
        List<@RUntainted String> list = new ArrayList<@RUntainted String>();

        int pos = 0;
        int len = s.length();
        do
        {
            int splitAt = s.indexOf(sep, pos);
            if (splitAt == -1)
            {
                list.add(s.substring(pos));
                return list;
            }
            list.add(s.substring(pos, splitAt));
            pos = splitAt + sep.length();
        }
        while (pos < len);
        return list;
    }
}
