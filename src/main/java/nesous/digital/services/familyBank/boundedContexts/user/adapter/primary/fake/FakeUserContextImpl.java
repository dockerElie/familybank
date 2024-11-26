package nesous.digital.services.familyBank.boundedContexts.user.adapter.primary.fake;

import nesous.digital.services.familyBank.boundedContexts.shareKernel.Email;
import nesous.digital.services.familyBank.boundedContexts.shareKernel.Text;
import nesous.digital.services.familyBank.boundedContexts.user.hexagon.domain.User;
import nesous.digital.services.familyBank.boundedContexts.user.hexagon.port.input.usecase.UserContext;
import nesous.digital.services.familyBank.boundedContexts.user.hexagon.value.object.Role;
import nesous.digital.services.familyBank.boundedContexts.user.hexagon.value.object.UserIdentifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component("fakeUserContextImpl")
public class FakeUserContextImpl implements UserContext {
    @Override
    public User getContext() {
        List<Role> roles = Stream.of("ROLE_MANAGER".toLowerCase())
                .map(Role::new)
                .toList();
        UserIdentifier userId = new UserIdentifier("1L");
        Text lastName = new Text("Elie Michel");
        Text firstName = new Text("NONO NOUAGONG");
        Text username = new Text("elno");
        Email email = new Email(new Text("elie@gmail.com"));
        return new User(userId, lastName, firstName, username, email, null, roles);
    }
}
