package db.dao;

import java.sql.*;
import java.sql.Types;

public final class AuditLogDao {

    private AuditLogDao() {}

    public static long insert(Connection connection, Long companyId, String eventType,
            String entityTable, String entityId, String actorHint, String payloadJson) throws SQLException {
        validateLengths(eventType, entityTable, entityId, actorHint, payloadJson);
        final String sql = "INSERT INTO audit_log (company_id, event_type, entity_table, entity_id, actor_hint, payload_json) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (companyId != null) { ps.setLong(1, companyId); }
            else { ps.setNull(1, Types.INTEGER); }
            ps.setString(2, eventType.trim());
            ps.setString(3, entityTable);
            ps.setString(4, entityId);
            ps.setString(5, actorHint);
            ps.setString(6, payloadJson);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
            }
        }
        throw new SQLException("audit_log insert did not return generated id");
    }

    private static void validateLengths(String eventType, String entityTable,
            String entityId, String actorHint, String payloadJson) {
        if (eventType == null || eventType.trim().isEmpty() || eventType.trim().length() > 128)
            throw new IllegalArgumentException("event_type 1-128 karakter olmalidir");
        if (entityTable != null && entityTable.length() > 128)
            throw new IllegalArgumentException("entity_table en fazla 128 karakter");
        if (entityId != null && entityId.length() > 256)
            throw new IllegalArgumentException("entity_id en fazla 256 karakter");
        if (actorHint != null && actorHint.length() > 256)
            throw new IllegalArgumentException("actor_hint en fazla 256 karakter");
        if (payloadJson != null && payloadJson.length() > 10_000)
            throw new IllegalArgumentException("payload_json en fazla 10000 karakter");
    }
}
