package one.tranic.mongoban.common.form;

import com.mongodb.lang.NonNull;
import one.tranic.mongoban.api.MongoBanAPI;
import one.tranic.mongoban.common.Collections;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.cumulus.form.Form;
import org.geysermc.cumulus.response.CustomFormResponse;

import java.util.List;
import java.util.function.Consumer;

public class GeyserForm {
    public static final List<String> typeList = Collections.newUnmodifiableList("Ban", "Warn");

    public static Form getSimpleForm(@NonNull Consumer<CustomFormResponse> resultHandler) {
        return CustomForm.builder()
                .title("MongoBan Console")
                .dropdown("Type", typeList)
                .input("Player")
                .validResultHandler(resultHandler)
                .build();
    }

    public static Form getDoForm() {
        return CustomForm.builder()
                .title("MongoBan Console")
                .dropdown("Type", typeList)
                .input("Player")
                .slider("Duration", 1, 3650)
                .dropdown("Duration unit", MongoBanAPI.TIME_SUGGEST)
                .input("Reason")
                .toggle("Ban IP")
                .validResultHandler(response -> {
                    DoForm formData = DoForm.from(response);
                })
                .build();
    }

    public static Form getUndoForm() {
        return getSimpleForm(response -> {
            // Todo
            SimpleForm formData = SimpleForm.from(response);
        });
    }

    public static Form getSearchForm() {
        return getSimpleForm(response -> {
            // Todo
            SimpleForm formData = SimpleForm.from(response);
        });
    }

    public record DoForm(int type, String player, int duration, int duration_unit, String reason, boolean banIp) {
        public static DoForm from(CustomFormResponse response) {
            return new DoForm(
                    response.asDropdown(0),
                    response.asInput(1),
                    (int) response.asSlider(2),
                    response.asDropdown(3),
                    response.asInput(4),
                    response.asToggle(5));
        }
    }

    public record SimpleForm(int type, String player) {
        public static SimpleForm from(CustomFormResponse response) {
            return new SimpleForm(response.asDropdown(0), response.asInput(1));
        }
    }
}
