package com.cetera.services;

import com.cetera.exceptions.PfmException;
import org.springframework.stereotype.Service;

/**
 * Created by danni on 5/23/16.
 */
@Service
public class AsciiValidationService {

    public void validateAscii(String jsonString) {
        for(int i = 0; i < jsonString.length(); i++) {
            /*
                for Admin to manage json string in db
                only allow ascii between 0-127 and register mark
            */
            int startingIndex = 0;
            int endingIndex = 127;
            int registerMark = 174;

            if ((jsonString.charAt(i)< startingIndex || jsonString.charAt(i) > endingIndex)
                && jsonString.charAt(i) != registerMark) {
                throw new PfmException("Invalid char: " + jsonString.charAt(i) + ", at "+i);
            }
        }
    }
}

