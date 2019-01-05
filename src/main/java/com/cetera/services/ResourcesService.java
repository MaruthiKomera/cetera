package com.cetera.services;

import com.cetera.domain.Resources;
import com.cetera.model.SsoRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * This service is used to create Resources seed data and get hash value for sso links
 * Created by danni on 4/5/16.
 */
public interface ResourcesService {
    void create(String dataSeededAsOfDate);
    SsoRequest getSsoValue(String target);

    //admin functions to manage resources data
    Resources add(Resources resources);
    List<Resources> getAll();
    Resources findOne(Long id);
    Resources update(Long id, MultipartFile newResourceLink);
    void delete(Long id);
}
