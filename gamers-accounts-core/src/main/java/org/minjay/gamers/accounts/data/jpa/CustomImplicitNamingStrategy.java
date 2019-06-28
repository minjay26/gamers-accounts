package org.minjay.gamers.accounts.data.jpa;

import org.hibernate.boot.model.naming.EntityNaming;
import org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl;

@SuppressWarnings("serial")
public class CustomImplicitNamingStrategy extends ImplicitNamingStrategyJpaCompliantImpl {

    private static final String PLURAL_SUFFIX = "s";
    
    @Override
    protected String transformEntityName(EntityNaming entityNaming) {
        String tableNameInSingularForm = super.transformEntityName(entityNaming);
        return transformToPluralForm(tableNameInSingularForm);
    }
    
    private String transformToPluralForm(String tableNameInSingularForm) {
        StringBuilder pluralForm = new StringBuilder();
 
        pluralForm.append(tableNameInSingularForm);
        if(!tableNameInSingularForm.endsWith(PLURAL_SUFFIX)){
            pluralForm.append(PLURAL_SUFFIX);
        }
        return pluralForm.toString();
    }
}
