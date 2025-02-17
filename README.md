# House of Representatives Polarization Analyzer

## About the Project

This project aims to provoke thought and analysis of polarization in the United States Congress, 
a topic which has only grown increasingly relevant as the country becomes increasingly politically polarized. 
In order to conduct this analysis, I chose to use a large data set of House of Representatives 
roll call votes, which is cited below, which includes votes ranging from 1953 to 2024. 

The program takes in two separate years from the user in the Java terminal, and then performs regression
in R to compare the presence of party unity votes among the roll call votes, which is when a majority 
of one party opposes the majority of another. The regression controls for near unanimous votes, where
90 percent or more of representatives vote the same way on a bill, in order to isolate the polarizing
votes. A limitation of this program is that it cannot control for long term changes in polarization
over time, since the regression only analyzes data from the two given years.

Once the two years are given, the regression table is printed to the terminal as well as a brief
analysis of the significance of the results. This program overall allows for quick, automated data 
analysis of polarization in Congress to allow the user to observe trends or patterns, which can
pave way for further studies or investigation.

Crespin, Michael H. and David Rohde. Political Institutions and Public Choice Roll-Call Database. 
Retrieved from https://ou.edu/carlalbertcenter/research/pipc-votes/.

## Setup

1. Install the Rserve package in RStudio
```
install.packages(Rserve)
```

2. Run the given code in a RScript or terminal to initialize
the R server:
```
library(Rserve)
Rserve(args="--vanilla")
```
3. Once the RServer is running, create a new Java project with the provided code and download the REngine
and RServeEngine JAR files from https://www.rforge.net/Rserve/files/. 


4. Add the jar files to the project's build path to connect Java to R. 
