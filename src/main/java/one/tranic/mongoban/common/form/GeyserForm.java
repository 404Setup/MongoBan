package one.tranic.mongoban.common.form;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import one.tranic.mongoban.api.MongoBanAPI;
import one.tranic.t.base.command.source.CommandSource;
import one.tranic.t.utils.Collections;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.cumulus.form.Form;
import org.geysermc.cumulus.form.ModalForm;
import org.geysermc.cumulus.response.CustomFormResponse;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class GeyserForm {
    public static final List<String> typeList = Collections.newUnmodifiableList("Ban", "Warn");
    public static final List<String> searchList = Collections.newUnmodifiableList("Target", "Operator");

    public static Form getSimpleForm(@NotNull Consumer<CustomFormResponse> resultHandler) {
        return CustomForm.builder()
                .title("MongoBan Console")
                .input("Target")
                .validResultHandler(resultHandler)
                .build();
    }

    public static <C extends CommandSource<?, ?>> Form getDoForm(Consumer<DoForm> consumer) {
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

    public static Form getMessageForm(Component message) {
        return getMessageForm(LegacyComponentSerializer.legacySection().serialize(message));
    }

    public static <C extends CommandSource<?, ?>> Form getUndoForm(Consumer<SimpleForm> consumer) {
        return getSimpleForm(response -> consumer.accept(SimpleForm.from(response)));
    }

    public static <C extends CommandSource<?, ?>> Form getSearchForm(C source, Consumer<SearchForm> consumer) {
        return CustomForm.builder()
                .title("MongoBan Search")
                .dropdown("Search Type", searchList)
                .validResultHandler(response ->
                        source.asPlayer().sendForm(getSimpleForm(resp ->
                                consumer.accept(SearchForm.from(response, resp))
                        ))
                ).build();
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

    public record SimpleForm(String player) {
        public static SimpleForm from(CustomFormResponse response) {
            return new SimpleForm(response.asInput(0));
        }
    }

    public record SearchForm(int searchType, int punishType, String player) {
        public static SearchForm from(CustomFormResponse response1, CustomFormResponse response2) {
            return new SearchForm(response1.asDropdown(0), response2.asDropdown(0), response2.asInput(1));
        }
    }
}
