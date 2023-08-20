package iie.controller;

import iie.Utils.Check;
import iie.bean.SearchFormData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.text.ParseException;
import java.text.SimpleDateFormat;

@Controller
@CrossOrigin
public class FromData {

    //http://localhost:8080/ReadingAssist/advancedsearch/searchAdvanced
    @PostMapping(value = "/ReadingAssist/advancedsearch/formData")
    //@ModelAttribute FormData formData
    public ResponseEntity<java.lang.String> postFlume(@ModelAttribute SearchFormData formData)
    {
        String errMessage =  Check.CheckParame(formData);
        if (!errMessage.equalsIgnoreCase("ok")){
            System.err.println(errMessage);
            return ResponseEntity.ok(errMessage);
        }




        return ResponseEntity.ok(formData.toString());
    }


}
