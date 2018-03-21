##Related tags 
================================================

A tag is a label provided by the reader of the given document. A document can have one or more tags. 
We would like to know the tags which are related to eachother.  Tags are related to eachother if they occur multiple times together.


If a document has tags "fruit" and "apple", and there are more other documents which also have "fruit" and "apple" as 
tags, we can say "fruit" and "apple" are related tags, because they occurred multiple times together. To avoid long running computation, 
we provide a limit which tells you how many related tags should be generated for each tag. The output should be tag pairs. 


**Example 1**

 - "doc1" tags => "apple", "fruit", "food"
 - "doc2" tags => "house", "building"
  maximumTagsPerTag: 1
  
 The output for this input if maximumTagsPerTag is "1":
 [("apple", "fruit"), ("fruit", "food"), ("food", "apple"), ("house", "building"), ("building", "house")]

**Example 2**
 
 Input:    [["apple", "fruit", "food"],
           ["house", "building"],
           ["apple", "fruit"],
           ["peach", "fruit", "sweet"],
           ["tower", " "],
           ["sweet", "fruit", "apple"],
           ["house", "home", "apple"],
           ["home", "building"]]
           
maximumTagsPerTag: 2

In this case "apple" is tagged together with "fruit" 3 times, with "food" once, "house" once etc.
You should choose maximum 2 tags per tag, so you should choose "fruit", since it has the highest occurrence number, and than "food", OR "house" etc., because they
have the second highest occurence number. If the tags have the same occurence number, you can pick randomly. 
So the output should contain ("apple", "fruit"), and ("apple", "food").
Let's check the tag "fruit". We will get "apple", and "sweet" as the most common tags. 
So the output should contain ("fruit", "apple"), and ("fruit", "sweet").
We should do the same thing for every other tags.

The output for the given input should be 
[ ("apple", "fruit"), ("apple", "food"), ("fruit", "apple"), ("fruit", "sweet"), ("food", "apple"),
  ("food", "fruit"), ("house", "building"), ("house", "home"), ("building", "house"), ("building", "home"),
  ("peach", "fruit"), ("peach", "sweet"), ("home", "house"), ("home", "apple")
]

#Solution:  
1. Load the tags into a dataframe in spark for each each row  
2. I consider for rows with only one tag that tag will match and empty tag as pair  
3. Each row of tags will generate a combination of 2 Tag,Tag  
4. I used reduceByKey in order to count how many times each a combination of Tag,Tag appears  
5. I grouped the resulted dataset on one of tags from the pair with the other one with count as value  
6. In order to get the most relevant maximumTagsPerTag tags I used the following reduce by key logic  
    => union the two parts of the reduce group values, sort by count, keep only the biggest maximumTagsPerTag
    
7. Return result mapped with the maximumTagsPerTag pairs sorted descending in the group
8. Write each partition from the dataSet and mergeFiles in final output file

#How to test it:  
mvn clean install -> to install the jar  
mvn test -> run the test  
mvn scala:run "-DaddArgs=maximumTagsPerTag|input|outputPath" -> run using arguments (Default will go 1, tags.csv from resources, result.csv at resources folder)   
