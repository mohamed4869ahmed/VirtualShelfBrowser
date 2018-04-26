package Utility;

import com.querydsl.core.types.dsl.BooleanExpression;

import java.util.ArrayList;
import java.util.List;

public class BookPredicatesBuilder {

    List<SearchCriteria> searchCriteriaList ;

    public BookPredicatesBuilder() {
        this.searchCriteriaList = new ArrayList<>();
    }

    public BookPredicatesBuilder with(SearchCriteria searchCriteria){
        searchCriteriaList.add(searchCriteria);
        return this;
    }

    public BooleanExpression build(){
        if (searchCriteriaList.isEmpty()){
            return null;
        }

        List<BooleanExpression> predicates = new ArrayList<>();
        for(SearchCriteria searchCriteria : searchCriteriaList){
            BookPredicate bookPredicate = new BookPredicate(searchCriteria);
            BooleanExpression booleanExpression = bookPredicate.getPredicate();
            if (booleanExpression == null){
                return null;
            }
            predicates.add(booleanExpression);
        }
        BooleanExpression result = predicates.get(0);
        for(BooleanExpression booleanExpression : predicates){
            result = result.and(booleanExpression);
        }
        return result;
    }

}
