package com.pat.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;

@Document(collection = "urllink")
public class UrlLink {
    @Id
    private String id;
    @NotNull
    private String urlLinkID;
    @NotNull
    private String linkDescription;
    @NotNull
    private String linkName;
    @NotNull
    private String url;
    @NotNull
    private String categoryLinkID;

    public UrlLink(String urlLinkID,String linkDescription,String linkName,String url,String categoryLinkID ){
        this.urlLinkID = urlLinkID;
        this.linkDescription = linkDescription;
        this.linkName = linkName;
        this.url = url;
        this.categoryLinkID = categoryLinkID;
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrlLinkID() {
        return urlLinkID;
    }

    public void setUrlLinkID(String urlLinkID) {
        this.urlLinkID = urlLinkID;
    }

    public String getLinkDescription() {
        return linkDescription;
    }

    public void setLinkDescription(String linkDescription) {
        this.linkDescription = linkDescription;
    }

    public String getLinkName() {
        return linkName;
    }

    public void setLinkName(String linkName) {
        this.linkName = linkName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCategoryLinkID() {
        return categoryLinkID;
    }

    public void setCategoryLinkID(String categoryLinkID) {
        this.categoryLinkID = categoryLinkID;
    }
}
