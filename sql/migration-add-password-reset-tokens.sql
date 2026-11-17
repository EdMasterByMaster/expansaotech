USE Expansaodb;
GO

IF OBJECT_ID(N'dbo.password_reset_token', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.password_reset_token (
        id_token BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT pk_password_reset_token PRIMARY KEY,
        id_usuario INT NOT NULL,
        token_hash VARCHAR(64) NOT NULL,
        created_at DATETIME2(3) NOT NULL,
        expires_at DATETIME2(3) NOT NULL,
        CONSTRAINT uq_password_reset_token_hash UNIQUE (token_hash),
        CONSTRAINT fk_password_reset_token_usuario FOREIGN KEY (id_usuario)
            REFERENCES dbo.Usuario(id_usuario) ON DELETE CASCADE
    );
END;
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'ix_password_reset_user_created' AND object_id = OBJECT_ID(N'dbo.password_reset_token'))
    CREATE INDEX ix_password_reset_user_created ON dbo.password_reset_token(id_usuario, created_at);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'ix_password_reset_expires_at' AND object_id = OBJECT_ID(N'dbo.password_reset_token'))
    CREATE INDEX ix_password_reset_expires_at ON dbo.password_reset_token(expires_at);
GO
