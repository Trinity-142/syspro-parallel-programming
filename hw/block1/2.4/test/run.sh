#!/bin/bash
cd graphs
#java -jar ../target/benchmarks.jar -rf csv -rff results.csv
python3 plot.py
pandoc answer.md -o ../answer.pdf --pdf-engine=xelatex -V mainfont="DejaVu Serif"
echo "Success! Open answer.pdf"