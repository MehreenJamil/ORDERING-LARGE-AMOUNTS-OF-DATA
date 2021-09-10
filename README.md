# ORDERING-LARGE-AMOUNTS-OF-DATA
This Program Implements "External merge sort" and "K-sort merging" algorithems ORDERING LARGE AMOUNTS OF DATA
ASSIGNMENT OVERVIEW

The lines in the file use the following format:
id<tab>timestamp<tab>data\r\n
Where id is a number (in hexadecimal notation), tab is a regular tab (represented by <tab>), and
timestamp is a Unix time stamp in milliseconds. Data is a string with different values for each entry.
At the end of every line there is a carriage return followed by a new line (represented by \r\n).
The file to order has a size of around 2GB

This program takes the largefile.txt file and generates a new file
that is ordered first by id then by date.
For example:
Id1<tab>date1<tab>data\r\n
Id1<tab>date2<tab>data\r\n
Id2<tab>date1<tab>data\r\n
Id2<tab>date2<tab>data\r\n
….

PROGRAM Restricted to Following requirements:

• sorts the lines in the new file by id and then by date.
• does not use more than 500 MB of RAM.
• does not use more than 5 GB of disk space (including input, output, and any temp file you might
use).
