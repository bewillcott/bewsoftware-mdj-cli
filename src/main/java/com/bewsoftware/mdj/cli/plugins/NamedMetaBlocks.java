/*
 *  File Name:    NamedMetaBlocks.java
 *  Project Name: bewsoftware-mdj-cli
 *
 *  Copyright (c) 2021 Bradley Willcott
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.bewsoftware.mdj.cli.plugins;

import com.bewsoftware.mdj.core.MarkdownProcessor;
import com.bewsoftware.mdj.core.TextEditor;
import java.util.regex.Pattern;

import static com.bewsoftware.mdj.cli.util.Constants.DISPLAY;
import static com.bewsoftware.mdj.cli.util.GlobalVariables.conf;
import static java.util.regex.Pattern.DOTALL;
import static java.util.regex.Pattern.compile;

/**
 * Process Named Meta Blocks.
 * <p>
 * A named meta block looks like each of the following:
 * <hr>
 * <pre><code>
 *
 * &#64;&#64;&#64;[#name]
 *
 * Some text.
 * Some more text.
 *
 * &#64;&#64;&#64;
 * </code></pre>
 * <hr>
 * The HTML from the above code will begin with something like this:
 * {@code <div id="name">}.<br>
 * Therefore, you can set up formatting via CSS using the 'id' value:
 * "{@code #name}".
 * <hr>
 * <pre><code>
 *
 * &#64;&#64;&#64;[@name]
 *
 * Some text.
 * Some more text.
 *
 * &#64;&#64;&#64;
 * </code></pre>
 * <hr>
 * The HTML from the above code will begin with something like this:
 * {@code <div class="name">}.<br>
 * Therefore, you can set up formatting via CSS using the 'class' value:
 * "{@code .name}".
 * <p>
 * The blank line before the beginning is <b>required</b>.<br>
 * '{@code name}' is used to refer to the block in the markup text for
 * substitution: ${page.name}.
 * <p>
 * <b>Note:</b> The label "{@code name} " used throughout this comment is an
 * example only.
 * You would use your own label as appropriate to your requirements.
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 1.1.7
 * @version 1.1.7
 */
public class NamedMetaBlocks implements Plugin
{
    public NamedMetaBlocks()
    {
        // NoOp
    }

    @Override
    public void execute()
    {
        TextEditor text = new TextEditor(
                conf.iniDoc.getString("page", "text", "")
        );
        Pattern p = compile(
                "(?<=\\n)(?:@@@\\[(?<type>[@#])(?<name>\\w+)\\]\\n(?<metablock>.*?)\\n@@@\\n)",
                DOTALL);

        text.replaceAll(p, m ->
        {
            String type = m.group("type");
            String name = m.group("name");
            String metaBlock = m.group("metablock");

            String html = "";

            if ("#".equals(type))
            {
                html = "\n<div id=\"" + name + "\">\n";
            } else if ("@".equals(type))
            {
                html = "\n<div class=\"" + name + "\">\n";
            }

            html += MarkdownProcessor.convert(metaBlock) + "\n</div>\n";

            DISPLAY.level(3)
                    .appendln("============================================")
                    .append("name: ").appendln(name)
                    .appendln("metablock:")
                    .appendln(html)
                    .println("============================================\n");

            conf.iniDoc.setString("page", name, html);
            return "";
        });

        text.replaceAll("\\\\@@@", "@@@");
        conf.iniDoc.setString("page", "text", text.toString());
    }
}
