package com.pat.repo;

import com.pat.domain.UrlLink;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "urllink", path = "urllink")
public interface UrlLinkRepository extends MongoRepository<UrlLink,String> {

}
