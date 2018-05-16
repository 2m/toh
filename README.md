# Towers of Hanoi puzzle solver

The program written in JAVA solves the Towers of Hanoi puzzle
using two different algorithms.

When using the recursive algorithm every level of the recursive
function call is shown. This makes it easy to follow along and
to understand how recursive algorithm works.

Iterative solution moves disks around in CW or CCW direction.

Interface is in English, Spanish and Lithuanian.

### Building and running

Note that this application requires at least Java 9 because
of the UTF8 encoding used in the resource bundle files.

Launch with:

```bash
$ mvn compile exec:java -Dexec.mainClass="lt.dvim.toh.TowersOfHanoi"
```

If you want to run the application in a different locale than
your system default locale, prepend the command with:

```bash
env LC_ALL=lt_LT.utf8
```
