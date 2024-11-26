package nesous.digital.services.familyBank.boundedContexts.user.adapter.primary.models;

import java.util.List;

public record UserPresenter(String identifier, String lastName, String firstName, String userName, String email,
                            List<String> roles, String status) {

}
