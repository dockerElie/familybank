package nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object;

import nesous.digital.services.familyBank.boundedContexts.shareKernel.Text;

public record Description(Text text) {

    public String value() {
        return text.value();
    }
}
