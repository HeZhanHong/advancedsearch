package test;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TermsQuery;
import co.elastic.clients.json.JsonData;

import java.util.ArrayList;
import java.util.List;

public class test {
    public static void main(String[] args) {


        List<FieldValue> fields = new ArrayList();
        fields.add(FieldValue.of("AA"));
        fields.add(FieldValue.of("BB"));


        RangeQuery rq =  RangeQuery.of(r -> r.field("news_publicdate").gte(JsonData.of("2023-08-10")).lte(JsonData.of("2023-08-20")));
        TermQuery tq = TermQuery.of(t -> t.field("news_type").value("AA"));
        TermQuery tq_null = TermQuery.of(t -> t.field("news_type").value("s"));
       // TermQuery tq_null_2 = TermQuery.of(null);
        TermsQuery sq1 = TermsQuery.of(t -> t.field("news_website_type").terms(x -> x.value(fields)));
        TermsQuery sq2 = TermsQuery.of(t -> t.field("news_website").terms(x -> x.value(fields)));




        System.out.println(rq);
        System.out.println(tq);
        System.out.println(tq_null);
        System.out.println(sq1);
        System.out.println(sq2);

    }
}
