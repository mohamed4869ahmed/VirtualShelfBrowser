package utility;

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
            double value;
            try {
                value = Double.parseDouble(criteria.getValue());
            } catch (NumberFormatException e) {
                return null;
            }
            switch (criteria.getOperation()) {
                case ":":
                    return path.eq(value);
                case ">":
                    return path.goe(value);
                case "<":
                    return path.loe(value);
            }
        } else {
            StringPath path = pathBuilder.getString(criteria.getKey());
            if (criteria.getOperation().equalsIgnoreCase(":")) {
                return path.equalsIgnoreCase(criteria.getValue());
            }
        }
        return null;
    }


}
