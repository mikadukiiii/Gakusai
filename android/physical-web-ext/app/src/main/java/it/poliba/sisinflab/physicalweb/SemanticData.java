package it.poliba.sisinflab.physicalweb;

import org.physical_web.physicalweb.InnerMetadataResolver;

/**
 * Created by giorgio on 13/02/15.
 */
public class SemanticData {

    private String annotation;
    private String refUrl;
    private double penalty;
    private String iconUrl;
    private InnerMetadataResolver.UrlMetadata refUrlMetadata;

    public SemanticData() {

    }

    public SemanticData(String annotation, double penalty) {
        this.annotation = annotation;
        this.penalty = penalty;

    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public double getPenalty() {
        return penalty;
    }

    public void setPenalty(double penalty) {
        this.penalty = penalty;
    }

    public String getRefUrl() {
        return refUrl;
    }

    public void setRefUrl(String refUrl) {
        this.refUrl = refUrl;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public InnerMetadataResolver.UrlMetadata getRefUrlMetadata() {
        return refUrlMetadata;
    }

    public void setRefUrlMetadata(InnerMetadataResolver.UrlMetadata refUrlMetadata) {
        this.refUrlMetadata = refUrlMetadata;
    }
}
