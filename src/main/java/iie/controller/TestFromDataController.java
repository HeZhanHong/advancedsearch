package iie.controller;

import iie.Utils.CheckUtil;
import iie.domain.SearchFormData;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@CrossOrigin
public class TestFromDataController {

    //http://localhost:8080/ReadingAssist/advancedsearch/searchAdvanced
    @PostMapping(value = "/ReadingAssist/advancedsearch/formData")
    //@ModelAttribute FormData formData
    public ResponseEntity<java.lang.String> postFlume(@ModelAttribute SearchFormData formData)
    {
        String errMessage =  CheckUtil.CheckParam(formData);
        if (!errMessage.equalsIgnoreCase("ok")){
            System.err.println(errMessage);
            return ResponseEntity.ok(errMessage);
        }




        return ResponseEntity.ok(formData.toString());
    }


}
