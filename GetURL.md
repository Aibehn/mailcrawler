**Descripción:**

Versión casi definitiva de la clase. Aparentemente funciona bien, extrae los enlaces a partir del `StringBuffer` de entrada, y los devuelve en una `LinkedList`.

Como parámetros de entrada, además del `StringBuffer` con el código, hemos añadido un String con la URL de la página en estudio, para que en caso de que se encuentre URLs relativas, las pueda convertir en absolutas.

Además hemos creado una función (`validURL`), que descarta enlaces a contenidos multimedia y cosas por el estilo, para evitar problemas.

---

**Funcionamiento:**

Para crear el objeto:

```
GetURL nuevoGetURL=new GetURL(stringbuffer_entrada,string_url);
```

Donde `stringbuffer_entrada` es el `StringBuffer` con el código del recurso y `string_url` es el `String` con la URL del recurso.

Para recuperar la lista con las URLs:

```
LinkedList<String> urls=new LinkedList<String>();
urls=nuevoGetURL.returnURL();
```

---

**posibles) Problemas:**

Podrían encontrarse enlaces no válidos, así que podría añadirse una función que abra una conexión y los compruebe antes de añadirlos a la lista. Podemos hacerlo dentro de esta clase o desde `MailCrawler_thread`; como mejor os venga.