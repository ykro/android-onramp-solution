# Cómo leer un ADR (`ARCHITECTURE_DECISIONS.md`)

En el curso encontrarás un archivo `ARCHITECTURE_DECISIONS.md` que documenta decisiones técnicas importantes usando el formato **ADR** ([Architecture Decision Records](https://adr.github.io/)). Cada entrada responde en pocas líneas qué se decidió, por qué y qué costo se acepta.

## Ejemplo concreto para Android

```markdown
# ADR-003: Guardar el catálogo en Room en lugar de un archivo JSON
- **Estado:** Aceptado (2026-03-10)
- **Contexto:** La app debe mostrar el catálogo sin conexión. Hoy lo guardamos
  como un archivo JSON y cada búsqueda lee y recorre el archivo completo.
- **Decisión:** Guardar los libros en Room (SQLite) con un índice por autor.
- **Alternativa descartada:** Mantener el archivo JSON. Es más simple, pero cada
  cambio reescribe el archivo entero y las búsquedas se vuelven lentas.
- **Consecuencias:**
  - Las búsquedas por autor dejan de recorrer todo el catálogo.
  - Cada cambio de esquema necesita una migración de Room.
```

Cuando abras `ARCHITECTURE_DECISIONS.md`, lee primero el **Contexto** para entender el problema, luego la **Decisión** para seguir la convención del repositorio y al final las **Consecuencias** para saber qué compensación asumió el equipo.

Vuelve al [README principal](../README.md) o consulta el [glosario](glosario.md).
