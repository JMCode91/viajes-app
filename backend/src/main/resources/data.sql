-- =====================================================================
-- Datos iniciales de la aplicacion.
-- Se ejecuta despues de que Hibernate crea/actualiza el esquema
-- (spring.jpa.defer-datasource-initialization=true).
--
-- NOTA ENCODING: archivo 100% ASCII. Los caracteres no-ASCII (acentos
-- espanoles y japones) se escriben con la sintaxis de escape Unicode de
-- PostgreSQL U&'...' (p. ej. '\00E9' = 'e', '\99C5' = 'u'). La BD es
-- PostgreSQL (UTF-8), por lo que se almacenan correctamente.
-- =====================================================================

-- ---------------------------------------------------------------------
-- Diccionario de supervivencia: categorias.
-- ---------------------------------------------------------------------
INSERT INTO categoria_diccionario (id, nombre, icono) VALUES
(1, 'Transporte', 'train-outline'),
(2, 'Restaurantes', 'restaurant-outline'),
(3, 'Compras', 'bag-handle-outline'),
(4, 'Emergencias', 'medkit-outline'),
(5, 'Alojamiento', 'bed-outline')
ON CONFLICT DO NOTHING;

-- ---------------------------------------------------------------------
-- Diccionario de supervivencia: frases (espanol / ingles / japones / romaji).
-- ---------------------------------------------------------------------
INSERT INTO frase_diccionario (id, categoria_diccionario_id, texto_espanol, texto_ingles, texto_japones, texto_japones_romaji) VALUES
-- TRANSPORTE
(1, 1, U&'\00BFD\00F3nde est\00E1 la estaci\00F3n de tren?', 'Where is the train station?', U&'\99C5\306F\3069\3053\3067\3059\304B\FF1F', 'Eki wa doko desu ka?'),
(2, 1, U&'\00BFQu\00E9 and\00E9n va a Tokio?', 'Which platform goes to Tokyo?', U&'\6771\4EAC\884C\304D\306E\30DB\30FC\30E0\306F\4F55\756A\3067\3059\304B\FF1F', 'Tokyo yuki no homu wa nan-ban desu ka?'),
(3, 1, U&'\00BFD\00F3nde puedo comprar un billete?', 'Where can I buy a ticket?', U&'\5207\7B26\306F\3069\3053\3067\8CB7\3048\307E\3059\304B\FF1F', 'Kippu wa doko de kaemasu ka?'),
(4, 1, U&'\00BFEste tren para en Osaka?', 'Does this train stop in Osaka?', U&'\3053\306E\96FB\8ECA\306F\5927\962A\306B\6B62\307E\308A\307E\3059\304B\FF1F', 'Kono densha wa Osaka ni tomarimasu ka?'),
(5, 1, U&'\00BFC\00F3mo llego a Universal Studios?', 'How do I get to Universal Studios?', U&'\30E6\30CB\30D0\30FC\30B5\30EB\30FB\30B9\30BF\30B8\30AA\306B\306F\3069\3046\884C\3051\3070\3044\3044\3067\3059\304B\FF1F', U&'Yunib\0101sharu sutajio ni wa d\014D ikeba ii desu ka?'),
(6, 1, U&'\00BFCu\00E1nto cuesta ir all\00ED?', 'How much is it to go there?', U&'\305D\3053\307E\3067\3044\304F\3089\3067\3059\304B\FF1F', 'Soko made ikura desu ka?'),
(7, 1, U&'\00BFD\00F3nde est\00E1 la parada de autob\00FAs?', 'Where is the bus stop?', U&'\30D0\30B9\505C\306F\3069\3053\3067\3059\304B\FF1F', 'Basutei wa doko desu ka?'),
(8, 1, 'Me he perdido.', 'I am lost.', U&'\9053\306B\8FF7\3044\307E\3057\305F\3002', 'Michi ni mayoimashita.'),
(9, 1, U&'\00BFA qu\00E9 hora sale el \00FAltimo tren?', 'What time is the last train?', U&'\7D42\96FB\306F\4F55\6642\3067\3059\304B\FF1F', U&'Sh\016Bden wa nanji desu ka?'),
(10, 1, U&'\00BFPuede mostrarme en el mapa?', 'Can you show me on the map?', U&'\5730\56F3\3067\6559\3048\3066\3082\3089\3048\307E\3059\304B\FF1F', 'Chizu de oshiete moraemasu ka?'),
-- RESTAURANTES
(11, 2, 'Agua del grifo, por favor.', 'Tap water, please.', U&'\304A\51B7\3092\304A\9858\3044\3057\307E\3059\3002', 'Ohiya o onegaishimasu.'),
(12, 2, 'La cuenta, por favor.', 'The bill, please.', U&'\304A\4F1A\8A08\3092\304A\9858\3044\3057\307E\3059\3002', 'Okaikei o onegaishimasu.'),
(13, 2, 'Una mesa para dos, por favor.', 'A table for two, please.', U&'2\540D\3067\3059\3002', 'Ni-mei desu.'),
(14, 2, U&'\00BFTienen men\00FA en ingl\00E9s?', 'Do you have an English menu?', U&'\82F1\8A9E\306E\30E1\30CB\30E5\30FC\306F\3042\308A\307E\3059\304B\FF1F', U&'Eigo no meny\016B wa arimasu ka?'),
(15, 2, U&'Soy al\00E9rgico a...', 'I am allergic to...', U&'\FF5E\306E\30A2\30EC\30EB\30AE\30FC\304C\3042\308A\307E\3059\3002', U&'~ no arerug\012B ga arimasu.'),
(16, 2, U&'\00BFQu\00E9 me recomienda?', 'What do you recommend?', U&'\304A\3059\3059\3081\306F\4F55\3067\3059\304B\FF1F', 'Osusume wa nan desu ka?'),
(17, 2, 'Estaba delicioso (al salir).', 'It was delicious.', U&'\3054\3061\305D\3046\3055\307E\3067\3057\305F\3002', U&'Gochis\014Dsama deshita.'),
(18, 2, U&'\00BFSe puede pagar con tarjeta?', 'Can I pay by card?', U&'\30AF\30EC\30B8\30C3\30C8\30AB\30FC\30C9\306F\4F7F\3048\307E\3059\304B\FF1F', U&'Kurejitto k\0101do wa tsukaemasu ka?'),
(19, 2, 'Sin picante, por favor.', 'Not spicy, please.', U&'\8F9B\304F\3057\306A\3044\3067\304F\3060\3055\3044\3002', 'Karaku shinaide kudasai.'),
(20, 2, 'Disculpe (para llamar al camarero).', 'Excuse me.', U&'\3059\307F\307E\305B\3093\3002', 'Sumimasen.'),
-- COMPRAS
(21, 3, U&'\00BFTienen tax-free?', 'Do you have tax-free?', U&'\514D\7A0E\306F\3067\304D\307E\3059\304B\FF1F', 'Menzei wa dekimasu ka?'),
(22, 3, U&'\00BFPuedo probarme esto?', 'Can I try this on?', U&'\8A66\7740\3057\3066\3082\3044\3044\3067\3059\304B\FF1F', 'Shichaku shite mo ii desu ka?'),
(23, 3, U&'\00BFCu\00E1nto cuesta esto?', 'How much is this?', U&'\3053\308C\306F\3044\304F\3089\3067\3059\304B\FF1F', 'Kore wa ikura desu ka?'),
(24, 3, 'Es demasiado caro.', 'It is too expensive.', U&'\9AD8\3059\304E\307E\3059\3002', 'Takasugimasu.'),
(25, 3, 'Lo compro.', 'I will take it.', U&'\3053\308C\3092\304F\3060\3055\3044\3002', 'Kore o kudasai.'),
(26, 3, U&'\00BFTienen esto en otra talla?', 'Do you have this in a different size?', U&'\5225\306E\30B5\30A4\30BA\306F\3042\308A\307E\3059\304B\FF1F', 'Betsu no saizu wa arimasu ka?'),
(27, 3, U&'\00BFD\00F3nde est\00E1 la secci\00F3n de videojuegos?', 'Where is the video games section?', U&'\30B2\30FC\30E0\30B3\30FC\30CA\30FC\306F\3069\3053\3067\3059\304B\FF1F', U&'G\0113mu k\014Dn\0101 wa doko desu ka?'),
(28, 3, 'Solo estoy mirando, gracias.', 'Im just looking, thank you.', U&'\898B\3066\3044\308B\3060\3051\3067\3059\3001\3042\308A\304C\3068\3046\3002', U&'Mite iru dake desu, arigat\014D.'),
(29, 3, U&'\00BFVenden gafas aqu\00ED?', 'Do you sell glasses here?', U&'\3053\3053\3067\30E1\30AC\30CD\306F\58F2\3063\3066\3044\307E\3059\304B\FF1F', 'Koko de megane wa utte imasu ka?'),
(30, 3, U&'\00BFPueden envolverlo para regalo?', 'Can you gift-wrap it?', U&'\30D7\30EC\30BC\30F3\30C8\7528\306B\5305\3093\3067\3082\3089\3048\307E\3059\304B\FF1F', U&'Purezento-y\014D ni tsutsunde moraemasu ka?'),
-- EMERGENCIAS
(31, 4, U&'Necesito un m\00E9dico.', 'I need a doctor.', U&'\533B\8005\3092\547C\3093\3067\304F\3060\3055\3044\3002', 'Isha o yonde kudasai.'),
(32, 4, 'He perdido mi pasaporte.', 'I lost my passport.', U&'\30D1\30B9\30DD\30FC\30C8\3092\306A\304F\3057\307E\3057\305F\3002', 'Pasupoto o nakushimashita.'),
(33, 4, U&'\00A1Ayuda!', 'Help!', U&'\52A9\3051\3066\FF01', 'Tasukete!'),
(34, 4, U&'Llame a la polic\00EDa.', 'Call the police.', U&'\8B66\5BDF\3092\547C\3093\3067\304F\3060\3055\3044\3002', 'Keisatsu o yonde kudasai.'),
(35, 4, 'Llame a una ambulancia.', 'Call an ambulance.', U&'\6551\6025\8ECA\3092\547C\3093\3067\304F\3060\3055\3044\3002', U&'Ky\016Bky\016Bsha o yonde kudasai.'),
(36, 4, 'Me han robado la cartera.', 'My wallet was stolen.', U&'\8CA1\5E03\3092\76D7\307E\308C\307E\3057\305F\3002', 'Saifu o nusumaremashita.'),
(37, 4, U&'\00BFD\00F3nde est\00E1 la embajada espa\00F1ola?', 'Where is the Spanish embassy?', U&'\30B9\30DA\30A4\30F3\5927\4F7F\9928\306F\3069\3053\3067\3059\304B\FF1F', 'Supein taishikan wa doko desu ka?'),
(38, 4, 'He perdido mi equipaje.', 'I lost my luggage.', U&'\8377\7269\3092\306A\304F\3057\307E\3057\305F\3002', 'Nimotsu o nakushimashita.'),
(39, 4, 'No me siento bien.', 'I dont feel well.', U&'\6C17\5206\304C\60AA\3044\3067\3059\3002', 'Kibun ga warui desu.'),
(40, 4, U&'\00BFHay alguna farmacia cerca?', 'Is there a pharmacy nearby?', U&'\8FD1\304F\306B\85AC\5C40\306F\3042\308A\307E\3059\304B\FF1F', 'Chikaku ni yakkyoku wa arimasu ka?'),
-- ALOJAMIENTO
(41, 5, 'Tengo una reserva.', 'I have a reservation.', U&'\4E88\7D04\3057\3066\3044\307E\3059\3002', 'Yoyaku shite imasu.'),
(42, 5, 'Quiero hacer el check-in.', 'I would like to check in.', U&'\30C1\30A7\30C3\30AF\30A4\30F3\3092\304A\9858\3044\3057\307E\3059\3002', 'Chekkuin o onegaishimasu.'),
(43, 5, U&'\00BFPodemos dejar las maletas aqu\00ED?', 'Can we leave our luggage here?', U&'\3053\3053\306B\8377\7269\3092\9810\3051\3066\3082\3044\3044\3067\3059\304B\FF1F', 'Koko ni nimotsu o azukete mo ii desu ka?'),
(44, 5, U&'\00BFCu\00E1l es la contrase\00F1a del Wi-Fi?', 'What is the Wi-Fi password?', U&'Wi-Fi\306E\30D1\30B9\30EF\30FC\30C9\306F\4F55\3067\3059\304B\FF1F', U&'Wi-Fi no pasuw\0101do wa nan desu ka?'),
(45, 5, 'El aire acondicionado no funciona.', 'The air conditioning is not working.', U&'\30A8\30A2\30B3\30F3\304C\52D5\304D\307E\305B\3093\3002', 'Eakon ga ugokimasen.'),
(46, 5, U&'Necesitamos m\00E1s toallas.', 'We need more towels.', U&'\30BF\30AA\30EB\3092\3082\3046\5C11\3057\304F\3060\3055\3044\3002', U&'Taoru o m\014D sukoshi kudasai.'),
(47, 5, U&'\00BFA qu\00E9 hora es el desayuno?', 'What time is breakfast?', U&'\671D\98DF\306F\4F55\6642\3067\3059\304B\FF1F', U&'Ch\014Dshoku wa nanji desu ka?'),
(48, 5, U&'\00BFA qu\00E9 hora es el check-out?', 'What time is check-out?', U&'\30C1\30A7\30C3\30AF\30A2\30A6\30C8\306F\4F55\6642\3067\3059\304B\FF1F', 'Chekkuauto wa nanji desu ka?'),
(49, 5, U&'Por favor, limpie mi habitaci\00F3n.', 'Please clean my room.', U&'\90E8\5C4B\3092\6383\9664\3057\3066\304F\3060\3055\3044\3002', U&'Heya o s\014Dji shite kudasai.'),
(50, 5, 'Necesito un adaptador de enchufe.', 'I need a plug adapter.', U&'\5909\63DB\30D7\30E9\30B0\304C\5FC5\8981\3067\3059\3002', U&'Henkan puragu ga hitsuy\014D desu.')
ON CONFLICT DO NOTHING;