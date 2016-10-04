# Search-Assignment2
Task 1: Implement your first search algorithm
Based on the Lucene index, we can start to design and implement efficient retrieval
algorithms. Let’s start from the easy ones. Please implement the following ranking
function using the Lucene index we provided through Canvas (index.zip):
𝐹 𝑞, 𝑑𝑜𝑐 =
𝑐(𝑡, 𝑑𝑜𝑐)
𝑙𝑒𝑛𝑔𝑡ℎ(𝑑𝑜𝑐)
∙ log (1 +
𝑁
𝑘 𝑡
)
!∈!
, where q is the user query, 𝑑𝑜𝑐 is the target (candidate document in AP89), 𝑡 is the query
term, 𝑐(𝑡, 𝑑𝑜𝑐) is the count of term 𝑡 in document 𝑑𝑜𝑐, N is total number of documents in
AP89, and 𝑘 𝑡 is the total number of documents that have the term 𝑡. Please use Lucene
API to get the information. From retrieval viewpoint,
!(!,!"#)
!"#$%!(!"#) is called normalized TF
(term frequency), while log (1 + !
! ! ) is IDF (inverse document frequency).
The following code (using Lucene API) can be useful to help you implement the ranking
function:
// Get the preprocessed query terms
Analyzer analyzer = new StandardAnalyzer();
QueryParser parser = new QueryParser("TEXT", analyzer);
Query query = parser.parse(queryString);
Set<Term> queryTerms = new LinkedHashSet<Term>();
query.extractTerms(queryTerms);
for (Term t : queryTerms) {
System.out.println(t.text());
}
IndexReader reader = DirectoryReader
.open(FSDirectory
.open(new File(pathToIndex)));
//Use DefaultSimilarity.decodeNormValue(…) to decode normalized document length
DefaultSimilarity dSimi=new DefaultSimilarity();
//Get the segments of the index
List<AtomicReaderContext> leafContexts = reader.getContext().reader()
.leaves();
for (int i = 0; i < leafContexts.size(); i++) {
AtomicReaderContext leafContext=leafContexts.get(i);
int startDocNo=leafContext.docBase;
int numberOfDoc=leafContext.reader().maxDoc();
for (int docId = startDocNo; docId < startDocNo+numberOfDoc; docId++) {
//Get normalized length for each document
float normDocLeng=dSimi.decodeNormValue(leafContext.reader()
.getNormValues("TEXT").get(docIdstartDocNo));
System.out.println("Normalized length for doc("+docId+") is
"+normDocLeng);
}
//Get the term frequency of "new" within each document containing it for
<field>TEXT</field>
DocsEnum de = MultiFields.getTermDocsEnum(leafContext.reader(),
MultiFields.getLiveDocs(leafContext.reader()),
"TEXT", new BytesRef("new"));
int doc;
while ((doc = de.nextDoc()) != DocsEnum.NO_MORE_DOCS) {
System.out.println("\"new\" occurs "+de.freq() + " times in doc(" +
(de.docID()+startDocNo)+") for the field TEXT");
}
}
For each given query, your code should be able to 1. Parse the query using Standard
Analyzer (Important: we need to use the SAME Analyzer that we used for indexing to
parse the query), 2. Calculate the relevance score for each query term, and 3. Calculate
the relevance score 𝐹 𝑞, 𝑑𝑜𝑐 .
The code for this task should be saved in a java class: easySearch.java
Task 2: Test your search function with TREC topics
Next, we will need to test the search performance with the TREC standardized topic
collections. You can download the query test topics from Canvas (topics.51-100).
In this collection, TREC provides a number of topics (total 50 topics), which can be
employed as the candidate queries for search tasks. For example, one TREC topic is:
<top>
<head> Tipster Topic Description
<num> Number: 054
<dom> Domain: International Economics
<title> Topic: Satellite Launch Contracts
<desc> Description:
Document will cite the signing of a contract or preliminary agreement, or the
making of a tentative reservation, to launch a commercial satellite.
<smry> Summary:
Document will cite the signing of a contract or preliminary agreement, or the
making of a tentative reservation, to launch a commercial satellite.
<narr> Narrative:
A relevant document will mention the signing of a contract or preliminary
agreement , or the making of a tentative reservation, to launch a commercial
satellite.
<con> Concept(s):
1. contract, agreement
2. launch vehicle, rocket, payload, satellite
3. launch services, commercial space industry, commercial launch industry
4. Arianespace, Martin Marietta, General Dynamics, McDonnell Douglas
5. Titan, Delta II, Atlas, Ariane, Proton
<fac> Factor(s):
<def> Definition(s):
</top>
In this task, you will need to use two different fields as queries: <title> and <desc>. The
former query is very short, while the latter one is much longer.
Your software must output up to top 1000 search results to a result file in a format that
enables the trec_eval program to produce evaluation reports. trec_eval expects its input to
be in the format described below.
QueryID Q0 DocID Rank Score RunID
For example:
10 Q0 DOC-NO1 1 0.23 run-1
10 Q0 DOC-NO2 2 0.53 run-1
10 Q0 DOC-NO3 3 0.15 run-1
: : : : : :
11 Q0 DOC-NOk 1 0.042 run-1
The code for this task should be saved in a java class: searchTRECtopics.java
Task 3: Test Other Search Algorithms
Next, we will test a number of other retrieval and ranking algorithms by using Lucene
API and the index provided through Canvas (index.zip).
For instance, you can use the following code to search the target corpus via BM25
algorithm.
IndexReader reader = DirectoryReader
.open(FSDirectory
.open(new File(pathToIndex)));
IndexSearcher searcher = new IndexSearcher(reader);
searcher.setSimilarity(new BM25Similarity()); //You need to explicitly specify the
ranking algorithm using the respective Similarity class
Analyzer analyzer = new StandardAnalyzer();
QueryParser parser = new QueryParser("TEXT", analyzer);
Query query = parser.parse(queryString);
TopScoreDocCollector collector = TopScoreDocCollector.create(1000, true);
searcher.search(query, collector);
ScoreDoc[] docs = collector.topDocs().scoreDocs;
for (int i = 0; i < docs.length; i++) {
Document doc = searcher.doc(docs[i].doc);
System.out.println(doc.get("DOCNO")+" "+docs[i].score);
}
reader.close();
In this task, you will test the following algorithms
1. Vector Space Model (org.apache.lucene.search.similarities.DefaultSimilarity)
2. BM25 (org.apache.lucene.search.similarities.BM25Similarity)
3. Language Model with Dirichlet Smoothing
(org.apache.lucene.search.similarities.LMDirichletSimilarity)
4. Language Model with Jelinek Mercer Smoothing
(org.apache.lucene.search.similarities.LMJelinekMercerSimilarity, set λ to 0.7)
You will need to compare the performance of those algorithms (and your algorithm
implemented in Task 1) with the TREC topics. For each topic, you will try two types of
queries: short query (<title> field), and long query (<desc> field). So, for each search
method, you will need to generate two separate result files, i.e., for BM25, you will need
to generate BM25longQuery.txt and BM25shortQuery.txt
The code for this task should be saved in a java class: compareAlgorithms.java
Task 4: Algorithm Evaluation
In this task, you will need to compare different retrieval algorithms via various evaluation
metrics, i.e., precision, recall, and MAP.
Please read this document about trec_eval:
http://faculty.washington.edu/levow/courses/ling573_SPR2011/hw/trec_eval_desc.htm
And, you can download the trec_eval program from
http://trec.nist.gov/trec_eval/trec_eval_latest.tar.gz
We will use this code to evaluate the search result performance.
TrecEval can be used via the command line in the following way:
trec_eval -m all_trec groundtruth.qrel results (the first parameter is the ground truth file or to
say judgment file, and the second parameter is the result file you just generated from the last task.
trec_eval --help should give you some ideas to choose the right parameters
You can download the ground truth file from Canvas (qrels.51-100).
