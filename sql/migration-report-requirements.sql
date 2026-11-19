USE Expansaodb;
GO
SET QUOTED_IDENTIFIER ON;
SET ANSI_NULLS ON;
GO

DECLARE @cpfUniqueConstraint sysname;
SELECT @cpfUniqueConstraint = kc.name
FROM sys.key_constraints kc
JOIN sys.index_columns ic
    ON ic.object_id = kc.parent_object_id AND ic.index_id = kc.unique_index_id
JOIN sys.columns c
    ON c.object_id = ic.object_id AND c.column_id = ic.column_id
WHERE kc.parent_object_id = OBJECT_ID(N'dbo.Usuario')
    AND kc.type = N'UQ'
    AND c.name = N'cpf'
    AND ic.key_ordinal = 1
    AND (SELECT COUNT(*) FROM sys.index_columns key_columns
         WHERE key_columns.object_id = ic.object_id
           AND key_columns.index_id = ic.index_id
           AND key_columns.key_ordinal > 0) = 1;

IF @cpfUniqueConstraint IS NOT NULL
BEGIN
    DECLARE @dropCpfConstraintSql nvarchar(300);
    SET @dropCpfConstraintSql = N'ALTER TABLE dbo.Usuario DROP CONSTRAINT '
        + QUOTENAME(@cpfUniqueConstraint);
    EXEC sys.sp_executesql @dropCpfConstraintSql;
END;

IF EXISTS (
    SELECT 1 FROM sys.columns
    WHERE object_id = OBJECT_ID(N'dbo.Usuario') AND name = N'cpf' AND is_nullable = 0
)
    ALTER TABLE dbo.Usuario ALTER COLUMN cpf VARCHAR(14) NULL;

IF NOT EXISTS (
    SELECT 1 FROM sys.indexes
    WHERE object_id = OBJECT_ID(N'dbo.Usuario') AND name = N'ux_usuario_cpf_not_null'
)
    CREATE UNIQUE INDEX ux_usuario_cpf_not_null ON dbo.Usuario(cpf) WHERE cpf IS NOT NULL;
GO

IF COL_LENGTH(N'dbo.Ong', N'razao_social') IS NULL ALTER TABLE dbo.Ong ADD razao_social NVARCHAR(200) NULL;
IF COL_LENGTH(N'dbo.Ong', N'nome_fantasia') IS NULL ALTER TABLE dbo.Ong ADD nome_fantasia NVARCHAR(150) NULL;
IF COL_LENGTH(N'dbo.Ong', N'cep') IS NULL ALTER TABLE dbo.Ong ADD cep VARCHAR(9) NULL;
IF COL_LENGTH(N'dbo.Ong', N'logradouro') IS NULL ALTER TABLE dbo.Ong ADD logradouro NVARCHAR(200) NULL;
IF COL_LENGTH(N'dbo.Ong', N'numero') IS NULL ALTER TABLE dbo.Ong ADD numero VARCHAR(20) NULL;
IF COL_LENGTH(N'dbo.Ong', N'complemento') IS NULL ALTER TABLE dbo.Ong ADD complemento NVARCHAR(100) NULL;
GO

IF COL_LENGTH(N'dbo.Usuario', N'telefone') IS NULL ALTER TABLE dbo.Usuario ADD telefone VARCHAR(20) NULL;
IF COL_LENGTH(N'dbo.Usuario', N'cargo') IS NULL ALTER TABLE dbo.Usuario ADD cargo NVARCHAR(100) NULL;
IF COL_LENGTH(N'dbo.Usuario', N'cep') IS NULL ALTER TABLE dbo.Usuario ADD cep VARCHAR(9) NULL;
IF COL_LENGTH(N'dbo.Usuario', N'logradouro') IS NULL ALTER TABLE dbo.Usuario ADD logradouro NVARCHAR(200) NULL;
IF COL_LENGTH(N'dbo.Usuario', N'numero') IS NULL ALTER TABLE dbo.Usuario ADD numero VARCHAR(20) NULL;
IF COL_LENGTH(N'dbo.Usuario', N'complemento') IS NULL ALTER TABLE dbo.Usuario ADD complemento NVARCHAR(100) NULL;
IF COL_LENGTH(N'dbo.Usuario', N'data_nascimento') IS NULL ALTER TABLE dbo.Usuario ADD data_nascimento DATE NULL;
IF COL_LENGTH(N'dbo.Usuario', N'email_verificado') IS NULL
    ALTER TABLE dbo.Usuario ADD email_verificado BIT NOT NULL CONSTRAINT df_usuario_email_verificado DEFAULT 1;
IF COL_LENGTH(N'dbo.Usuario', N'ativo') IS NULL
    ALTER TABLE dbo.Usuario ADD ativo BIT NOT NULL CONSTRAINT df_usuario_ativo DEFAULT 1;
IF COL_LENGTH(N'dbo.Usuario', N'perfil_completo') IS NULL
    ALTER TABLE dbo.Usuario ADD perfil_completo BIT NOT NULL CONSTRAINT df_usuario_perfil_completo DEFAULT 1;
IF COL_LENGTH(N'dbo.Usuario', N'tentativas_login') IS NULL
    ALTER TABLE dbo.Usuario ADD tentativas_login INT NOT NULL CONSTRAINT df_usuario_tentativas_login DEFAULT 0;
IF COL_LENGTH(N'dbo.Usuario', N'bloqueado_ate') IS NULL ALTER TABLE dbo.Usuario ADD bloqueado_ate DATETIME2(3) NULL;
GO

IF OBJECT_ID(N'dbo.usuario_area_interesse', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.usuario_area_interesse (
        id_usuario INT NOT NULL,
        area NVARCHAR(80) NOT NULL,
        CONSTRAINT pk_usuario_area_interesse PRIMARY KEY (id_usuario, area),
        CONSTRAINT fk_usuario_area_interesse_usuario FOREIGN KEY (id_usuario)
            REFERENCES dbo.Usuario(id_usuario) ON DELETE CASCADE
    );
END;
GO

IF OBJECT_ID(N'dbo.usuario_dia_disponivel', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.usuario_dia_disponivel (
        id_usuario INT NOT NULL,
        dia_semana VARCHAR(12) NOT NULL,
        CONSTRAINT pk_usuario_dia_disponivel PRIMARY KEY (id_usuario, dia_semana),
        CONSTRAINT fk_usuario_dia_disponivel_usuario FOREIGN KEY (id_usuario)
            REFERENCES dbo.Usuario(id_usuario) ON DELETE CASCADE
    );
END;
GO

IF OBJECT_ID(N'dbo.account_activation_token', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.account_activation_token (
        id_token BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT pk_account_activation_token PRIMARY KEY,
        id_usuario INT NOT NULL,
        token_hash VARCHAR(64) NOT NULL,
        created_at DATETIME2(3) NOT NULL,
        expires_at DATETIME2(3) NOT NULL,
        CONSTRAINT uq_account_activation_token_hash UNIQUE (token_hash),
        CONSTRAINT fk_account_activation_token_usuario FOREIGN KEY (id_usuario)
            REFERENCES dbo.Usuario(id_usuario) ON DELETE CASCADE
    );
END;
GO

IF COL_LENGTH(N'dbo.Curso', N'modalidade') IS NULL
    ALTER TABLE dbo.Curso ADD modalidade VARCHAR(20) NOT NULL CONSTRAINT df_curso_modalidade DEFAULT 'INFORMATICA';
IF COL_LENGTH(N'dbo.Curso', N'carga_horaria') IS NULL ALTER TABLE dbo.Curso ADD carga_horaria INT NULL;
IF COL_LENGTH(N'dbo.Curso', N'vagas') IS NULL ALTER TABLE dbo.Curso ADD vagas INT NULL;
IF COL_LENGTH(N'dbo.Curso', N'faixa_etaria_min') IS NULL ALTER TABLE dbo.Curso ADD faixa_etaria_min INT NULL;
IF COL_LENGTH(N'dbo.Curso', N'faixa_etaria_max') IS NULL ALTER TABLE dbo.Curso ADD faixa_etaria_max INT NULL;
IF COL_LENGTH(N'dbo.Curso', N'data_inicio') IS NULL ALTER TABLE dbo.Curso ADD data_inicio DATE NULL;
IF COL_LENGTH(N'dbo.Curso', N'data_termino') IS NULL ALTER TABLE dbo.Curso ADD data_termino DATE NULL;
IF COL_LENGTH(N'dbo.Curso', N'hora_inicio') IS NULL ALTER TABLE dbo.Curso ADD hora_inicio TIME(0) NULL;
IF COL_LENGTH(N'dbo.Curso', N'hora_fim') IS NULL ALTER TABLE dbo.Curso ADD hora_fim TIME(0) NULL;
IF COL_LENGTH(N'dbo.Curso', N'publicado') IS NULL
    ALTER TABLE dbo.Curso ADD publicado BIT NOT NULL CONSTRAINT df_curso_publicado DEFAULT 1;
GO

IF OBJECT_ID(N'dbo.curso_dia_semana', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.curso_dia_semana (
        id_curso INT NOT NULL,
        dia_semana VARCHAR(12) NOT NULL,
        CONSTRAINT pk_curso_dia_semana PRIMARY KEY (id_curso, dia_semana),
        CONSTRAINT fk_curso_dia_semana_curso FOREIGN KEY (id_curso)
            REFERENCES dbo.Curso(id_curso) ON DELETE CASCADE
    );
END;
GO

IF COL_LENGTH(N'dbo.Evento', N'data_inicio') IS NULL ALTER TABLE dbo.Evento ADD data_inicio DATETIME2(3) NULL;
IF COL_LENGTH(N'dbo.Evento', N'data_termino') IS NULL ALTER TABLE dbo.Evento ADD data_termino DATETIME2(3) NULL;
IF COL_LENGTH(N'dbo.Evento', N'voluntarios_necessarios') IS NULL
    ALTER TABLE dbo.Evento ADD voluntarios_necessarios INT NOT NULL CONSTRAINT df_evento_voluntarios_necessarios DEFAULT 0;
IF COL_LENGTH(N'dbo.Evento', N'publicado') IS NULL
    ALTER TABLE dbo.Evento ADD publicado BIT NOT NULL CONSTRAINT df_evento_publicado DEFAULT 1;
GO

IF OBJECT_ID(N'dbo.matricula_curso', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.matricula_curso (
        id_matricula BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT pk_matricula_curso PRIMARY KEY,
        id_curso INT NOT NULL,
        id_usuario INT NOT NULL,
        data_matricula DATETIME2(3) NOT NULL CONSTRAINT df_matricula_curso_data DEFAULT SYSUTCDATETIME(),
        CONSTRAINT uq_matricula_curso_usuario UNIQUE (id_curso, id_usuario),
        CONSTRAINT fk_matricula_curso_curso FOREIGN KEY (id_curso)
            REFERENCES dbo.Curso(id_curso) ON DELETE CASCADE,
        CONSTRAINT fk_matricula_curso_usuario FOREIGN KEY (id_usuario)
            REFERENCES dbo.Usuario(id_usuario) ON DELETE CASCADE
    );
END;
GO

IF OBJECT_ID(N'dbo.inscricao_evento', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.inscricao_evento (
        id_inscricao BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT pk_inscricao_evento PRIMARY KEY,
        id_evento INT NOT NULL,
        id_usuario INT NOT NULL,
        data_inscricao DATETIME2(3) NOT NULL CONSTRAINT df_inscricao_evento_data DEFAULT SYSUTCDATETIME(),
        CONSTRAINT uq_inscricao_evento_usuario UNIQUE (id_evento, id_usuario),
        CONSTRAINT fk_inscricao_evento_evento FOREIGN KEY (id_evento)
            REFERENCES dbo.Evento(id_evento) ON DELETE CASCADE,
        CONSTRAINT fk_inscricao_evento_usuario FOREIGN KEY (id_usuario)
            REFERENCES dbo.Usuario(id_usuario) ON DELETE CASCADE
    );
END;
GO
