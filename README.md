#The Relational Data Wrangler
##Eden Zik and Kahlil Oppenheimer
[![PDF Status](https://www.sharelatex.com/github/repos/edenzik/128Project/builds/latest/badge.svg)](https://www.sharelatex.com/github/repos/edenzik/128Project/builds/latest/output.pdf)
==========

[Current Application](http://edenzik.github.io/128Project/wrangler/index.html)

Preliminary relevant files

[Proposal](https://www.sharelatex.com/github/repos/edenzik/128Project/builds/latest/output.pdf)

Overview
=====
- Wrangle data into relational form
- 

Build Process
======
1.  Open Eclipse
2.  Go to File -> Import -> Existing Maven Project
3.  Select the "wizard" folder
4.  Once all is imported, click on "run" (green arrow)
5.  It will ask you for a build goal - in the window type the build goal as "jetty:run"
6.  Press the green arrow again and you're done!

Hard Functional Dependecy Detection
=========
1.  Detect hard functional dependencies
  1.  Iterate over all pairs of attributes (all permutations of two attributes)
  2.  For each pair a and b, run a query to detect if there are more than one value of b determined by a (not FD therefore)
  3.  Record all sucessfull FD's to run statistical analysis later.
2.  If two attributes are not a functional dependency, but have high corrolation
  1.  Typo detection - detect if a->b is true, but ruled false only because b can be a typo
  2.  Detect typo using edit distance + some clustering
  3.  Prompt the user to verify otherwise.
  4.  Another method of detecting typo's- use typo frequency in the word (more typos likely to be in the middle) to rule if it's a typo.
