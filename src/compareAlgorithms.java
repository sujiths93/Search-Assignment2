import java.io.FileWriter;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.search.similarities.LMJelinekMercerSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.FSDirectory;
public class compareAlgorithms {
	public class storequery{
		String title;
		String desc;
		String qid;
		storequery(String title,String desc,String qid){
			this.title=title;
			this.desc=desc;
			this.qid=qid;
		}
	}
	public ArrayList<storequery> parsequery() throws IOException {
		ArrayList<storequery> ob = new ArrayList<storequery>();
		String s="C:\\Users\\sujit\\Desktop\\Search\\Assignment 2\\topics.txt";
		String content = new String(Files.readAllBytes(Paths.get(s)));
		content=content.trim().replaceAll("\n", "\n");
		content = content.replace("\n", "");
		String[] tokens=content.split("<top>");
		for(int i=1;i<tokens.length;i++){
			String id="",title="",desc="";
			final Pattern pattern2 = Pattern.compile("<num>\\s+Number:((.*))<dom>");
			final java.util.regex.Matcher matcher2 = pattern2.matcher(tokens[i]);
			if(((java.util.regex.Matcher) matcher2).find()){
				id=matcher2.group(1);
			}
			final Pattern pattern = Pattern.compile("<title>\\s+Topic:((.*))<desc>");
			final java.util.regex.Matcher matcher = pattern.matcher(tokens[i]);
			if(((java.util.regex.Matcher) matcher).find()){
				title=matcher.group(1);
			}
			final Pattern pattern1 = Pattern.compile("<desc>\\s+Description:((.*))<smry>");
			final java.util.regex.Matcher matcher1 = pattern1.matcher(tokens[i]);
			if(((java.util.regex.Matcher) matcher1).find()){
				desc=matcher1.group(1);
			}
			ob.add(new storequery(title,desc,id));
		}
		return ob;
	}
	public void compareAlgosshortQuery(String fileName,Similarity similarity,ArrayList<storequery> queryObject) throws ParseException, IOException{
		String index = "C:\\Users\\sujit\\Desktop\\Search\\Assignment 2\\index";
		IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(index)));
		IndexSearcher searcher = new IndexSearcher(reader);
		Analyzer analyzer = new StandardAnalyzer();
		searcher.setSimilarity(similarity);
		QueryParser parser = new QueryParser("TEXT", analyzer);
		FileWriter fwriter = new FileWriter(fileName);
		for(storequery sq:queryObject){
			//System.out.println("\n"+sq.qid+"\n");
			Query query = parser.parse(QueryParser.escape(sq.title));
			TopDocs results = searcher.search(query, 1000);		
			//Print number of hits
			int numTotalHits = results.totalHits;
			//System.out.println(numTotalHits + " total matching documents");
			//Print retrieved results
			int count=0;
			ScoreDoc[] hits = results.scoreDocs;
			for(int i=0;i<hits.length;i++){	
				int j=i+1;
				String temp="";
				temp+=sq.qid+" doc="+hits[i].doc+" rank="+j+" score="+hits[i].score+"\n";
				fwriter.append(temp);
				count++;
			}
		}
		fwriter.close();
		reader.close();
		System.out.println("DONE");
	}
	public void compareAlgoslongQuery(String fileName,Similarity similarity,ArrayList<storequery> queryObject) throws ParseException, IOException{
		String index = "C:\\Users\\sujit\\Desktop\\Search\\Assignment 2\\index";
		IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(index)));
		IndexSearcher searcher = new IndexSearcher(reader);
		Analyzer analyzer = new StandardAnalyzer();
		searcher.setSimilarity(similarity);
		QueryParser parser = new QueryParser("TEXT", analyzer);
		FileWriter fwriter = new FileWriter(fileName);
		for(storequery sq:queryObject){
			Query query = parser.parse(QueryParser.escape(sq.desc));
			TopDocs results = searcher.search(query, 1000);		
			//Print number of hits
			int numTotalHits = results.totalHits;
			//System.out.println(numTotalHits + " total matching documents");
			int count=0;
			ScoreDoc[] hits = results.scoreDocs;
			for(int i=0;i<hits.length;i++){	
				int j=i+1;
				String temp="";
				temp+=sq.qid+" doc="+hits[i].doc+" rank="+j+" score="+hits[i].score+"\n";
				fwriter.append(temp);
			}
		}
		fwriter.close();
		reader.close();
		
		System.out.println("DONE");
	}
	public static void main(String args[]) throws IOException, ParseException{
		compareAlgorithms c=new compareAlgorithms();
		ArrayList<storequery> o=new ArrayList<storequery>();
		o=c.parsequery();
		for(int i=0;i<o.size();i++){
			o.get(i).desc=o.get(i).desc.trim();
			o.get(i).qid=o.get(i).qid.trim();
			o.get(i).title=o.get(i).title.trim();
			//System.out.println(o.get(i).qid+"\n"+o.get(i).title+"\n"+o.get(i).desc);
		}
		/*Short Query*/
		Similarity similarity=new BM25Similarity();
		c.compareAlgosshortQuery("C:\\Users\\sujit\\Desktop\\Search\\Assignment 2\\BM25longshortQuery.txt",similarity,o);
		
		//Default Similarity has deprecated, using Classic Similarity Instead.
		similarity=new ClassicSimilarity();
		c.compareAlgosshortQuery("C:\\Users\\sujit\\Desktop\\Search\\Assignment 2\\ClassicSimilarityshortQuery.txt",similarity,o);

		similarity=new LMDirichletSimilarity();
		c.compareAlgosshortQuery("C:\\Users\\sujit\\Desktop\\Search\\Assignment 2\\LMDirichletSimilarityshortQuery.txt",similarity,o);
		
		similarity=new LMJelinekMercerSimilarity((float) 0.7);
		c.compareAlgosshortQuery("C:\\Users\\sujit\\Desktop\\Search\\Assignment 2\\LMJelinekMercerSimilarityshortQuery.txt",similarity,o);
		
		/*Long Query*/
		similarity=new BM25Similarity();
		c.compareAlgoslongQuery("C:\\Users\\sujit\\Desktop\\Search\\Assignment 2\\BM25longlongQuery.txt",similarity,o);
		
		similarity=new ClassicSimilarity();
		c.compareAlgoslongQuery("C:\\Users\\sujit\\Desktop\\Search\\Assignment 2\\ClassicSimilaritylongQuery.txt",similarity,o);

		similarity=new LMDirichletSimilarity();
		c.compareAlgoslongQuery("C:\\Users\\sujit\\Desktop\\Search\\Assignment 2\\LMDirichletSimilaritylongQuery.txt",similarity,o);
		
		similarity=new LMJelinekMercerSimilarity((float) 0.7);
		c.compareAlgoslongQuery("C:\\Users\\sujit\\Desktop\\Search\\Assignment 2\\LMJelinekMercerSimilaritylongQuery.txt",similarity,o);
		
	}
}
