import java.io.File;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.ConstantScoreWeight;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Weight;
import org.apache.lucene.search.similarities.DFISimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
class Scores{
	String docId;
	double score;
	public  Scores(String docId,double score){
		this.docId=docId;
		this.score=score;
	}
}
class CustomComparator implements Comparator<Scores> {
    @Override
    public int compare(Scores a, Scores b) {
        if(a.score<b.score)
        	return 1;
        else
        	if(a.score>b.score)
        		return -1;
        return 0;
    }
}
public class easySearch {
	static String indexpath;
	String queryString;
	static IndexReader reader;
	static IndexSearcher searcher;
	static int totalNumDocs;
	easySearch(String queryString) throws IOException, ParseException{
		indexpath="C:\\Users\\sujit\\Desktop\\Search\\Assignment 2\\index";
		this.queryString=queryString;
		reader=DirectoryReader.open(FSDirectory.open(Paths.get(indexpath)));
		searcher=new IndexSearcher(reader);
		totalNumDocs=reader.maxDoc();
	}
	
	public Set<Term> parserQuery(String queryString) throws IOException, ParseException{
		Query query;
		Analyzer analyzer=new StandardAnalyzer();
		QueryParser parser=new QueryParser("TEXT",analyzer);
		query=parser.parse(queryString);
		Set<Term> queryTerms=new LinkedHashSet<Term>();
		searcher.createNormalizedWeight(query, false).extractTerms(queryTerms);
		return queryTerms;
	}
	public int termfreq(Term t,LeafReaderContext leaf,int startdoc,int docNum) throws IOException{
		PostingsEnum de=MultiFields.getTermDocsEnum(leaf.reader(), "TEXT", new BytesRef(t.text()));
		int doc;
		if(de!=null){
			while((doc=de.nextDoc())!=PostingsEnum.NO_MORE_DOCS){
				if(de.docID()+startdoc==docNum)
					return de.freq();
			}
		}
		return 0;
	}
	public double tfIdfQterm(int docFreq,int termFreq,float docLength){
		double result=(termFreq/docLength)*Math.log(1+(totalNumDocs/docFreq));
		return result;
	}
	public List<Scores> scoreTopDocs() throws IOException, ParseException{
		Set<Term> queryTerms;
		ArrayList<Double> scorelist=new ArrayList<Double>();
		queryTerms=parserQuery(queryString);
		ClassicSimilarity cSimi = new ClassicSimilarity();
		List<LeafReaderContext> leafContexts = reader.getContext().reader().leaves();
		List<Scores> scores=new ArrayList<Scores>();
		for(int i=0;i<leafContexts.size();i++){
			LeafReaderContext leafContext=leafContexts.get(i);
			int startDoc=leafContext.docBase;
			int numberofDoc=leafContext.reader().numDocs();
			for(int docId=0;docId<numberofDoc;docId++){
				float normDocLength=cSimi.decodeNormValue(leafContext.reader().getNormValues("TEXT").get(docId));
				float docLength = 1 / (normDocLength * normDocLength);
				double sum=0.0;
				for(Term t:queryTerms){
					int docFreq=reader.docFreq(new Term("TEXT",t.text()));
					int termFreq=termfreq(t,leafContext,startDoc,startDoc+docId);
					sum+=tfIdfQterm(docFreq,termFreq,docLength);
				}
				Set<String> s=new TreeSet<String>();
				s.add("DOCNO");
				Document doc=leafContext.reader().document(docId,s);
				scores.add(new Scores(doc.get("DOCNO"),sum));
			}
		}
		Collections.sort(scores, new CustomComparator());
		return scores;
	}
	public static void main(String args[]) throws IOException, ParseException{
			easySearch es=new easySearch("hello world");
			List<Scores> finalScores=es.scoreTopDocs();
			for(int i=0;i<500;i++){
				System.out.println("DOC-ID "+finalScores.get(i).docId+" Score "+finalScores.get(i).score);
			}
    }
}

