package one.tranic.mongoban.common.form;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import one.tranic.mongoban.api.MongoBanAPI;
import one.tranic.mongoban.api.command.source.SourceImpl;
import one.tranic.mongoban.common.Collections;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.cumulus.form.Form;
import org.geysermc.cumulus.form.ModalForm;
import org.geysermc.cumulus.response.CustomFormResponse;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class GeyserForm {
    public static final List<String> typeList = Collections.newUnmodifiableList("Ban", "Warn");

    public static Form getSimpleForm(@NotNull Consumer<CustomFormResponse> resultHandler) {
        return CustomForm.builder()
                .title("MongoBan Console")
                .dropdown("Type", typeList)
                .input("Player")
                .validResultHandler(resultHandler)
                .build();
    }

    public static <C extends SourceImpl<?, ?>> Form getDoForm(C source, Consumer<DoForm> consumer) {
        return CustomForm.builder()
                .title("MongoBan Console")
                .input("Player")
                .slider("Duration", 1, 3650)
                .dropdown("Duration unit", MongoBanAPI.TIME_SUGGEST)
                .input("Reason")
                .toggle("Strict")
                .validResultHandler(response -> consumer.accept(DoForm.from(response)))
                .build();
    }

    public static Form getMessageForm(String message) {
        return ModalForm.builder().title("MongoBan Message").content(message).build();
    }

    public static Form getMessageForm(TextComponent message) {
        return getMessageForm(LegacyComponentSerializer.legacySection().serialize(message));
    }

    public static <C extends SourceImpl<?, ?>> Form getUndoForm(C source, Consumer<SimpleForm> consumer) {
        return getSimpleForm(response -> consumer.accept(SimpleForm.from(response)));
    }

    public static <C extends SourceImpl<?, ?>> Form getSearchForm(C source, Consumer<SimpleForm> consumer) {
        return getSimpleForm(response -> consumer.accept(SimpleForm.from(response)));
    }

    public record DoForm(String player, int duration, String duration_unit, String reason, boolean strict) {
        public static DoForm from(CustomFormResponse response) {
            return new DoForm(
                    response.asInput(0),
                    (int) response.asSlider(1),
                    MongoBanAPI.TIME_SUGGEST.get(response.asDropdown(2)),
                    response.asInput(3),
                    response.asToggle(4));
        }
    }

    public record SimpleForm(int type, String player) {
        public static SimpleForm from(CustomFormResponse response) {
            return new SimpleForm(response.asDropdown(0), response.asInput(1));
        }
    }
}
