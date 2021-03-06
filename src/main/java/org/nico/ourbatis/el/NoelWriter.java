package org.nico.ourbatis.el;

import java.util.List;
import java.util.function.Function;

import org.nico.ourbatis.Ourbatis;
import org.nico.ourbatis.adapter.AssistAdapter;
import org.nico.ourbatis.xml.Document;
import org.nico.ourbatis.xml.DocumentType;

/**
 * Renders and writes the entire template file
 * 
 * @author nico
 */
public class NoelWriter {

	private List<Document> documents;
	
	private StringBuilder builder = new StringBuilder();
	
	public NoelWriter(List<Document> documents) {
		this.documents = documents;
	}
	
	public void append(String body) {
		this.builder.append(body);
	}
	
	public String body() {
		return builder.toString();
	}
	
	public NoelWriter write(Function<Document, String> specialCallBack, Function<String, String> thenCallBack){
		write(this.documents, specialCallBack);
		this.builder = new StringBuilder(thenCallBack.apply(body()));
		return this;
	}
	
	public NoelWriter writeProperties(AssistAdapter adapter){
		
		return this;
	}
	
	public String format() {
		return new NoelFormat(body())
				.format()
				.result();
	}
	
	private NoelWriter write(List<Document> documents, Function<Document, String> specialCallBack){
		for(int index = 0; index < documents.size(); index ++){
			Document document = documents.get(index);
			builder.append(document.getBeforeContent());
			if(Ourbatis.ASSIST_ADAPTERS.containsKey(document.getName())) {
				builder.append(specialCallBack.apply(document));
			}else {
				String paramStr = document.getParameterString();
				if(document.getType() == DocumentType.SINGLE){
					builder.append("<" + document.getName() + " " + paramStr + document.getTail() + ">");
				}else{
					builder.append("<" + document.getName() + " " + paramStr + " >");
					if(document.getChilds() != null && ! document.getChilds().isEmpty()){
						write(document.getChilds(), specialCallBack);
					}else{
						builder.append(document.getContent());
					}
					builder.append("</"+ document.getName() + ">");
				}
			}
			if(index == documents.size() - 1) {
				builder.append(document.getAfterContent());
			}
		}
		return this;
	}
	
}
