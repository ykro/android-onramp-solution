# Cómo leer un ADR en Android (`ARCHITECTURE_DECISIONS.md`)

En [ykro/android-course](https://github.com/ykro/android-course) encontrarás archivos `ARCHITECTURE_DECISIONS.md` con registros cortos llamados **ADR** (*Architecture Decision Records*), documentados en [adr.github.io](https://adr.github.io/). Un ADR explica **por qué** el equipo eligió una herramienta o un diseño en un momento dado, qué descartó y qué consecuencias acepta.

## Ejemplo concreto de 15 líneas para Android

```markdown
# ADR-003: Usar Room con ON CONFLICT DO UPDATE para sincronizar libros
- **Estado:** Aceptado (2026-03-10)
- **Contexto:** La app descarga el catálogo desde la API REST y permite al
  usuario marcar favoritos y guardar notas locales sin conexión.
- **Decisión:** Guardamos los libros en Room (SQLite) y sincronizamos con
  `ON CONFLICT(id) DO UPDATE SET title = excluded.title, author = excluded.author`.
- **Alternativa descartada:** `INSERT OR REPLACE` en SQLite borra la fila
  previa y reinicia las columnas locales `favorite` y `local_note`.
- **Consecuencias:**
  - Positiva: los favoritos y notas del usuario sobreviven a cada recarga.
  - Positiva: podemos probar la consulta en milisegundos con SQLite en JVM.
  - Trade-off: el `@Dao` necesita una consulta `@Query` explícita en lugar
    de usar solo `@Insert(onConflict = OnConflictStrategy.REPLACE)`.
```

## Cómo leerlo rápido antes de tocar código

1. **Estado:** confirma si la decisión sigue vigente (`Aceptado`) o ya fue reemplazada (`Superado por ADR-XXX`).
2. **Contexto y alternativa descartada:** te ahorran volver a proponer una opción que el equipo ya probó y rechazó.
3. **Consecuencias:** indican qué pruebas, contratos o reglas debes mantener cuando agregues una nueva pantalla o módulo.

Si quieres repasar otros términos del repositorio, vuelve al [glosario](glosario.md) o al [README principal](../README.md).
