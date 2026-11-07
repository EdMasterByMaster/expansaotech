IF DB_ID(N'Expansaodb') IS NULL
    CREATE DATABASE Expansaodb;
GO

USE Expansaodb;
GO

IF OBJECT_ID(N'dbo.Ong', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.Ong (
        id_ong INT IDENTITY(1,1) NOT NULL CONSTRAINT pk_ong PRIMARY KEY,
        cnpj VARCHAR(18) NOT NULL,
        nome NVARCHAR(150) NOT NULL,
        email VARCHAR(100) NOT NULL,
        telefone VARCHAR(20) NULL,
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
        cpf VARCHAR(14) NOT NULL,
        nome NVARCHAR(150) NOT NULL,
        email VARCHAR(100) NOT NULL,
        senha VARCHAR(255) NOT NULL,
        tipo VARCHAR(30) NOT NULL,
        foto_url VARCHAR(500) NULL,
        data_cadastro DATETIME2(3) NOT NULL CONSTRAINT df_usuario_data_cadastro DEFAULT SYSUTCDATETIME(),
        CONSTRAINT uq_usuario_cpf UNIQUE (cpf),
        CONSTRAINT uq_usuario_email UNIQUE (email),
        CONSTRAINT chk_tipo_usuario CHECK (
            tipo IN ('Funcionario', 'Administracao', 'Colaborador', 'Voluntario', 'Professor', 'Doador', 'Aluno')
        )
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
        data_criacao DATETIME2(3) NOT NULL CONSTRAINT df_curso_data_criacao DEFAULT SYSUTCDATETIME(),
        CONSTRAINT fk_curso_ong FOREIGN KEY (id_ong)
            REFERENCES dbo.Ong(id_ong) ON DELETE CASCADE,
        CONSTRAINT fk_curso_professor FOREIGN KEY (id_professor)
            REFERENCES dbo.Professor(id_usuario)
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
        local_evento NVARCHAR(255) NOT NULL,
        foto_url VARCHAR(500) NULL,
        data_criacao DATETIME2(3) NOT NULL CONSTRAINT df_evento_data_criacao DEFAULT SYSUTCDATETIME(),
        CONSTRAINT fk_evento_ong FOREIGN KEY (id_ong)
            REFERENCES dbo.Ong(id_ong) ON DELETE CASCADE
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
