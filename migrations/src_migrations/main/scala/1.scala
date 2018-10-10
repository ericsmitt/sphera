import slick.jdbc.PostgresProfile.api._
import com.liyaos.forklift.slick.SqlMigration
import slick.jdbc
import slick.jdbc.PostgresProfile

object M1 {
  def createPersistenceMetadataTable: DBIO[Int] =
    sqlu"""CREATE TABLE IF NOT EXISTS "persistence_metadata" (
      persistence_key BIGSERIAL NOT NULL,
      persistence_id VARCHAR(255) NOT NULL,
      sequence_nr BIGINT NOT NULL,
      PRIMARY KEY (persistence_key),
      UNIQUE (persistence_id));"""

  def createPersistenceJournalTable: DBIO[Int] =
    sqlu"""CREATE TABLE IF NOT EXISTS "persistence_journal" (
      persistence_key BIGINT NOT NULL REFERENCES "persistence_metadata"(persistence_key),
      sequence_nr BIGINT NOT NULL,
      message BYTEA NOT NULL,
      PRIMARY KEY (persistence_key, sequence_nr));"""

  def createPersistenceSnapshotTable: DBIO[Int] =
    sqlu"""CREATE TABLE IF NOT EXISTS "persistence_snapshot" (
      persistence_key BIGINT NOT NULL REFERENCES "persistence_metadata"(persistence_key),
      sequence_nr BIGINT NOT NULL,
      created_at BIGINT NOT NULL,
      snapshot BYTEA NOT NULL,
      PRIMARY KEY (persistence_key, sequence_nr));"""

  def createTables: Seq[DBIO[Int]] = Seq(
    createPersistenceMetadataTable,
    createPersistenceJournalTable,
    createPersistenceSnapshotTable)

  MyMigrations.migrations = MyMigrations.migrations :+ SqlMigration(1)(createTables)
}
