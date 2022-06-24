/*
 *  File Name:    FileData.java
 *  Project Name: bewsoftware-mdj-cli
 *
 *  Copyright (c) 2022 Bradley Willcott
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

package com.bewsoftware.mdj.cli.util;

import java.nio.file.Path;

/**
 * FileData class description.
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 1.0
 * @version 1.1.8
 */
@SuppressWarnings("PublicField")
public class FileData
{
    public Path destinationPath;

    public Path sourcePath;

    public FileData(Path sourcePath, Path destinationPath)
    {
        this.sourcePath = sourcePath;
        this.destinationPath = destinationPath;
    }

}
