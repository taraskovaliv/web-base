package dev.kovaliv.view.def;

import j2html.tags.DomContent;
import lombok.Getter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Head {

    @Getter
    private static final Set<DomContent> additionalTags = new HashSet<>();

    public static void addAdditionalTags(Collection<DomContent> additionalTags) {
        if (additionalTags != null) {
            additionalTags.addAll(additionalTags);
        }
    }
}
