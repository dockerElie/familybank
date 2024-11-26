package nesous.digital.services.familyBank.boundedContexts.shareKernel.commons;

import nesous.digital.services.familyBank.boundedContexts.shareKernel.Text;

public class TextGuard extends BaseGuard<Text> {

    public TextGuard(Text value) {
        super(value);
    }

    public void againstNullOrWhitespace(String message) {
        if (value == null) {
            againstNull(message);
        }
        assert value != null;
        against(value::isNullOrWhitespace, message);
    }
}
