-- =====================================================================
-- Datos iniciales del diccionario de supervivencia (idioma JA).
-- Se ejecuta solo al inicializar la base de datos vacia.
-- Nota: la tabla 'diccionario' requiere uuid-ossp/gen_random_uuid de PostgreSQL.
-- =====================================================================

INSERT INTO diccionario (id, frase_espanol, frase_japones, pronunciacion, categoria, idioma) VALUES
    (gen_random_uuid(), 'Hola', 'こんにちは', 'konnichiwa', 'SALUDOS', 'JA'),
    (gen_random_uuid(), 'Buenos días', 'おはようございます', 'ohayou gozaimasu', 'SALUDOS', 'JA'),
    (gen_random_uuid(), 'Buenas noches', 'こんばんは', 'konbanwa', 'SALUDOS', 'JA'),
    (gen_random_uuid(), 'Gracias', 'ありがとう', 'arigatou', 'CORTESIA', 'JA'),
    (gen_random_uuid(), 'Por favor', 'お願いします', 'onegaishimasu', 'CORTESIA', 'JA'),
    (gen_random_uuid(), 'Perdón', 'すみません', 'sumimasen', 'CORTESIA', 'JA'),
    (gen_random_uuid(), 'Sí', 'はい', 'hai', 'BASICO', 'JA'),
    (gen_random_uuid(), 'No', 'いいえ', 'iie', 'BASICO', 'JA'),
    (gen_random_uuid(), '¿Dónde está el baño?', 'トイレはどこですか？', 'toire wa doko desu ka?', 'VIAJE', 'JA'),
    (gen_random_uuid(), '¿Cuánto cuesta?', 'いくらですか？', 'ikura desu ka?', 'VIAJE', 'JA'),
    (gen_random_uuid(), 'La cuenta, por favor', 'お会計お願いします', 'okaikei onegaishimasu', 'RESTAURANTE', 'JA'),
    (gen_random_uuid(), 'Una cerveza', 'ビールを一つください', 'biiru o hitotsu kudasai', 'RESTAURANTE', 'JA'),
    (gen_random_uuid(), 'No entiendo', 'わかりません', 'wakarimasen', 'EMERGENCIA', 'JA'),
    (gen_random_uuid(), 'Ayuda', '助けて', 'tasukete', 'EMERGENCIA', 'JA');
