IF DB_ID(N'Expansaodb') IS NULL
    CREATE DATABASE Expansaodb;
GO

USE Expansaodb;
GO
SET QUOTED_IDENTIFIER ON;
SET ANSI_NULLS ON;
GO

IF OBJECT_ID(N'dbo.Ong', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.Ong (
        id_ong INT IDENTITY(1,1) NOT NULL CONSTRAINT pk_ong PRIMARY KEY,
        cnpj VARCHAR(18) NOT NULL,
        nome NVARCHAR(150) NOT NULL,
        razao_social NVARCHAR(200) NULL,
        nome_fantasia NVARCHAR(150) NULL,
        email VARCHAR(100) NOT NULL,
        telefone VARCHAR(20) NULL,
        cep VARCHAR(9) NULL,
        logradouro NVARCHAR(200) NULL,
        numero VARCHAR(20) NULL,
        complemento NVARCHAR(100) NULL,
        foto_url VARCHAR(500) NULL,
        data_cadastro DATETIME2(3) NOT NULL CONSTRAINT df_ong_data_cadastro DEFAULT SYSUTCDATETIME(),
        CONSTRAINT uq_ong_cnpj UNIQUE (cnpj)
    );
END;
GO

IF OBJECT_ID(N'dbo.Usuario', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.Usuario (
        id_usuario INT IDENTITY(1,1) NOT NULL CONSTRAINT pk_usuario PRIMARY KEY,
        cpf VARCHAR(14) NULL,
        nome NVARCHAR(150) NOT NULL,
        email VARCHAR(100) NOT NULL,
        senha VARCHAR(255) NOT NULL,
        tipo VARCHAR(30) NOT NULL,
        foto_url VARCHAR(500) NULL,
        telefone VARCHAR(20) NULL,
        cargo NVARCHAR(100) NULL,
        cep VARCHAR(9) NULL,
        logradouro NVARCHAR(200) NULL,
        numero VARCHAR(20) NULL,
        complemento NVARCHAR(100) NULL,
        data_nascimento DATE NULL,
        email_verificado BIT NOT NULL CONSTRAINT df_usuario_email_verificado DEFAULT 1,
        ativo BIT NOT NULL CONSTRAINT df_usuario_ativo DEFAULT 1,
        perfil_completo BIT NOT NULL CONSTRAINT df_usuario_perfil_completo DEFAULT 1,
        tentativas_login INT NOT NULL CONSTRAINT df_usuario_tentativas_login DEFAULT 0,
        bloqueado_ate DATETIME2(3) NULL,
        data_cadastro DATETIME2(3) NOT NULL CONSTRAINT df_usuario_data_cadastro DEFAULT SYSUTCDATETIME(),
        CONSTRAINT uq_usuario_email UNIQUE (email),
        CONSTRAINT chk_tipo_usuario CHECK (
            tipo IN ('Funcionario', 'Administracao', 'Colaborador', 'Voluntario', 'Professor', 'Doador', 'Aluno')
        )
    );
END;
GO

IF NOT EXISTS (
    SELECT 1 FROM sys.indexes
    WHERE object_id = OBJECT_ID(N'dbo.Usuario') AND name = N'ux_usuario_cpf_not_null'
)
    CREATE UNIQUE INDEX ux_usuario_cpf_not_null ON dbo.Usuario(cpf) WHERE cpf IS NOT NULL;
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

IF OBJECT_ID(N'dbo.Professor', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.Professor (
        id_usuario INT NOT NULL CONSTRAINT pk_professor PRIMARY KEY,
        especialidade NVARCHAR(255) NOT NULL,
        CONSTRAINT fk_professor_usuario FOREIGN KEY (id_usuario)
            REFERENCES dbo.Usuario(id_usuario) ON DELETE CASCADE
    );
END;
GO

IF OBJECT_ID(N'dbo.Curso', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.Curso (
        id_curso INT IDENTITY(1,1) NOT NULL CONSTRAINT pk_curso PRIMARY KEY,
        id_ong INT NOT NULL,
        id_professor INT NOT NULL,
        titulo NVARCHAR(150) NOT NULL,
        descricao NVARCHAR(MAX) NULL,
        horario NVARCHAR(100) NOT NULL,
        local_curso NVARCHAR(255) NOT NULL,
        foto_url VARCHAR(500) NULL,
        modalidade VARCHAR(20) NOT NULL CONSTRAINT df_curso_modalidade DEFAULT 'INFORMATICA',
        carga_horaria INT NULL,
        vagas INT NULL,
        faixa_etaria_min INT NULL,
        faixa_etaria_max INT NULL,
        data_inicio DATE NULL,
        data_termino DATE NULL,
        hora_inicio TIME(0) NULL,
        hora_fim TIME(0) NULL,
        publicado BIT NOT NULL CONSTRAINT df_curso_publicado DEFAULT 1,
        data_criacao DATETIME2(3) NOT NULL CONSTRAINT df_curso_data_criacao DEFAULT SYSUTCDATETIME(),
        CONSTRAINT fk_curso_ong FOREIGN KEY (id_ong)
            REFERENCES dbo.Ong(id_ong) ON DELETE CASCADE,
        CONSTRAINT fk_curso_professor FOREIGN KEY (id_professor)
            REFERENCES dbo.Professor(id_usuario)
    );
END;
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

IF OBJECT_ID(N'dbo.Evento', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.Evento (
        id_evento INT IDENTITY(1,1) NOT NULL CONSTRAINT pk_evento PRIMARY KEY,
        id_ong INT NOT NULL,
        titulo NVARCHAR(150) NOT NULL,
        descricao NVARCHAR(MAX) NULL,
        data_hora DATETIME2(3) NOT NULL,
        data_inicio DATETIME2(3) NULL,
        data_termino DATETIME2(3) NULL,
        local_evento NVARCHAR(255) NOT NULL,
        foto_url VARCHAR(500) NULL,
        voluntarios_necessarios INT NOT NULL CONSTRAINT df_evento_voluntarios_necessarios DEFAULT 0,
        publicado BIT NOT NULL CONSTRAINT df_evento_publicado DEFAULT 1,
        data_criacao DATETIME2(3) NOT NULL CONSTRAINT df_evento_data_criacao DEFAULT SYSUTCDATETIME(),
        CONSTRAINT fk_evento_ong FOREIGN KEY (id_ong)
            REFERENCES dbo.Ong(id_ong) ON DELETE CASCADE
    );
END;
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

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'ix_curso_id_ong' AND object_id = OBJECT_ID(N'dbo.Curso'))
    CREATE INDEX ix_curso_id_ong ON dbo.Curso(id_ong);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'ix_curso_id_professor' AND object_id = OBJECT_ID(N'dbo.Curso'))
    CREATE INDEX ix_curso_id_professor ON dbo.Curso(id_professor);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'ix_evento_id_ong_data_hora' AND object_id = OBJECT_ID(N'dbo.Evento'))
    CREATE INDEX ix_evento_id_ong_data_hora ON dbo.Evento(id_ong, data_hora);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'ix_password_reset_user_created' AND object_id = OBJECT_ID(N'dbo.password_reset_token'))
    CREATE INDEX ix_password_reset_user_created ON dbo.password_reset_token(id_usuario, created_at);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'ix_password_reset_expires_at' AND object_id = OBJECT_ID(N'dbo.password_reset_token'))
    CREATE INDEX ix_password_reset_expires_at ON dbo.password_reset_token(expires_at);
GO
