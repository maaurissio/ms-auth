INSERT INTO credenciales (
    id,
    usuario_id,
    email,
    password_hash,
    estado,
    creado_en
) VALUES (
    gen_random_uuid(),
    gen_random_uuid(),
    'admin@cordillera.cl',
    '$2a$10$evLt6ua7C4hNi76HZytOl.aCmhaooqh6qC/ZeB0Pxh2E87XsXQCEO',
    'ACTIVO',
    now()
);