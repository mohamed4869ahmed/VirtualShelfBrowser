package Utility;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.StringPath;
import model.Book;


public class BookPredicate {
    private SearchCriteria criteria;

    public BookPredicate(SearchCriteria searchCriteria) {
        this.criteria = searchCriteria;
    }

    public BooleanExpression getPredicate() {
        PathBuilder<Book> pathBuilder = new PathBuilder<Book>(Book.class, "book");
        if (criteria.isDouble()) {
            NumberPath<Double> path = pathBuilder.getNumber(criteria.getKey(), Double.class);
            double value = Double.parseDouble(criteria.getValue().toString());
            switch (criteria.getOperation()) {
                case ":":
                    return path.eq(value);
                case ">":
                    return path.gt(value);
                case "<":
                    return path.lt(value);
            }
        } else {
            StringPath path = pathBuilder.getString(criteria.getKey());
            if (criteria.getOperation().equalsIgnoreCase(":")) {
                return path.containsIgnoreCase(criteria.getValue().toString());
            }
        }
        return null;
    }


}
