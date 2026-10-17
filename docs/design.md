# Design direction

Composed with `create-frontend` (design-direction.js, type `events`, name `NitteSync`)
for the existing Jakarta Faces UI. The stack is fixed by the project brief
(JSF, no JavaScript frameworks), so this records the visual direction, not a stack choice.

| Axis | Choice | Reason |
|---|---|---|
| Theme | Citrus (library), light only | Campus events app for students and clubs — the theme library maps this project type to Citrus: tangerine action on warm off-white, aubergine ink, grape accent |
| Hero | split | index.xhtml reads as copy beside one action; no photography exists and the events table has no image column, so the copy carries it |
| Navigation | inline | At most six links, chosen by the signed-in role; a collapsed menu would hide the app's primary navigation behind an extra tap |
| Rhythm | columns | The event list is a card grid; columns structure it without inventing sections the data does not have |
| Cards | elevated | Citrus shape is soft elevation at 8/12/20px radii; the event card is the main content unit |
| Buttons | soft (rounded) | Follows the theme radius; the one main action per view is filled, everything else is outlined or a link |
| Type | all-sans / regular | The theme pairs Red Hat Display (headings) with Red Hat Text (body) — one voice at two sizes; a 1.25 scale keeps tables and forms readable |
| Imagery | none | No image column in the schema; artwork would be decoration that does not aid use |
| Motion | minimal + focus-ring | The app is server-rendered with full-page postbacks, so there is no client animation layer to justify more; the card hover lift is the single recipe |
| Density | regular | Card grid plus data tables; 68rem container |

Alternatives offered: **(B)** Birch · showcase · rules · tinted cards · all-sans/tight;
**(C)** Citrus · showcase · columns · elevated cards · all-serif/display.

## Tokens

`src/main/webapp/resources/css/style.css` holds the palette, type scale, spacing,
radii, shadows and motion tokens as CSS custom properties. Components reference
tokens only; no literal colours outside `:root`.

Motion: 120 / 200 / 320 ms, `ease-out` for entrances, `ease-in` for exits,
4px travel — all collapsed to 1ms under `prefers-reduced-motion: reduce`.

## Contrast

Checked with the skill's `scripts/check-contrast.js` against the Citrus token set
(light mode, `pass: true`):

- body text 13.62:1 · lowest text pair 5.08:1 (`successForeground` on `success`)
- lowest UI pair 3.06:1 (`input` on `background`)

Two warnings applied in the stylesheet: focus rings are drawn with a 2px offset in
`background` (the ring is close to `primary` in lightness), and `success` is never
used as text inside a muted fill — status colours appear only as fills carrying
their own foreground.