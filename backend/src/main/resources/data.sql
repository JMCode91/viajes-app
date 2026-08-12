-- =====================================================================
-- Datos iniciales de la aplicacion.
-- Se ejecuta despues de que Hibernate crea/actualiza el esquema
-- (spring.jpa.defer-datasource-initialization=true).
-- =====================================================================
--
-- NOTA ENCODING: para que este archivo sea 100% ASCII (independiente del
-- encoding con el que el parser de scripts lea el fichero), los caracteres
-- no-ASCII (acentos espanoles y japones) se escriben con la sintaxis de
-- escape Unicode de PostgreSQL U&'...' (p. ej. '\00E9' = 'e', '\99C5' = 'u').
-- La base de datos destino es PostgreSQL (UTF-8), por lo que se almacenan
-- correctamente.
-- =====================================================================

-- ---------------------------------------------------------------------
-- Diccionario de supervivencia: categorias.
-- ---------------------------------------------------------------------
INSERT INTO categoria_diccionario (id, nombre, icono) VALUES
(1, 'Transporte', 'train-outline'),
(2, 'Restaurantes', 'restaurant-outline'),
(3, 'Compras', 'bag-handle-outline'),
(4, 'Emergencias', 'medkit-outline')
ON CONFLICT DO NOTHING;

-- ---------------------------------------------------------------------
-- Diccionario de supervivencia: frases (espanol / ingles / japones / romaji).
-- ---------------------------------------------------------------------
INSERT INTO frase_diccionario (id, categoria_diccionario_id, texto_espanol, texto_ingles, texto_japones, texto_japones_romaji) VALUES
(1, 1, U&'\00BFD\00F3nde est\00E1 la estaci\00F3n de tren?', 'Where is the train station?', U&'\99C5\306F\3069\3053\3067\3059\304B\FF1F', 'Eki wa doko desu ka?'),
(2, 1, U&'\00BFQu\00E9 and\00E9n va a Tokio?', 'Which platform goes to Tokyo?', U&'\6771\4EAC\884C\304D\306E\30DB\30FC\30E0\306F\4F55\756A\3067\3059\304B\FF1F', 'Tokyo yuki no homu wa nan-ban desu ka?'),
(3, 2, 'Agua del grifo, por favor.', 'Tap water, please.', U&'\304A\6D99\3092\304A\9858\3044\3057\307E\3059\3002', 'Ohiya o onegaishimasu.'),
(4, 2, 'La cuenta, por favor.', 'The bill, please.', U&'\304A\4F1A\8A08\3092\304A\9858\3044\3057\307E\3059\3002', 'Okaikei o onegaishimasu.'),
(5, 3, U&'\00BFTienen tax-free?', 'Do you have tax-free?', U&'\514D\7A0E\306F\3067\304D\307E\3059\304B\FF1F', 'Menzei wa dekimasu ka?'),
(6, 3, U&'\00BFPuedo probarme esto?', 'Can I try this on?', U&'\8A66\7740\3057\3066\3082\3044\3044\3067\3059\304B\FF1F', 'Shichaku shite mo ii desu ka?'),
(7, 4, U&'Necesito un m\00E9dico.', 'I need a doctor.', U&'\533B\8005\3092\547C\3093\3067\304F\3060\3055\3044\3002', 'Isha o yonde kudasai.'),
(8, 4, 'He perdido mi pasaporte.', 'I lost my passport.', U&'\30D1\30B9\30DD\30FC\30C8\3092\306A\304F\3057\307E\3057\305F\3002', 'Pasupoto o nakushimashita.')
ON CONFLICT DO NOTHING;
