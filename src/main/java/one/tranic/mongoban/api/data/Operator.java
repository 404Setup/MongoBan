package one.tranic.mongoban.api.data;

import java.util.UUID;

/**
 * Represents an operator responsible for actions in a system or database.
 * <p>
 * This record is used to encapsulate the details of an operator, such as their display name
 * and unique identifier (UUID). Operators typically perform administrative or management
 * tasks, like issuing bans, warnings, or other moderation actions.
 * <p>
 * Instances of this record are immutable, ensuring consistent and safe handling
 * of operator-related data within the application.
 *
 * @param name The display name of the operator.
 * @param uuid The unique identifier of the operator.
 */
public record Operator(String name, UUID uuid) {
}
