Analysis is done based on 500 input messages

N-Gram Tokenizer:
It occupied 1.8 GB of disk space. 
Accuracy: 18.8%
Execution Time: 657s for 500 input messages, extremely slow.
Left around 18 input messages without any match.
Check the logs in n_gram.log in Elastic_Search_POC folder. 

Default Analyzer with whitespace tokenizer:
It occupied 700 MB of disk space. 
Accuracy: 71.4%
Execution Time: 24s for 500 input messages, extremely slow.
Left around 0 input messages without any match.
Check the logs in default_analyzer.log, information in prod_templates_index_without_shingles.txt for settings and stats in Elastic_Search_POC folder. 

Default Analyzer with whitespace tokenizer and shingle filter:
It occupied 250 MB of disk space. 
Accuracy: 71.6%
Execution Time: 32s for 500 input messages, extremely slow.
Left around 0 input messages without any match.
Check the logs in default_analyzer_with_shingles.log, information in prod_templates_index_with_shingles.txt for settings and stats in Elastic_Search_POC folder. 


