# SA-Arasaka — Guide Utilisateur

> Système de gestion et réservation de places de parking

---

## Table des matières

1. [Présentation](#présentation)
2. [Connexion](#connexion)
3. [Rôles et permissions](#rôles-et-permissions)
4. [Tableau de bord parking](#tableau-de-bord-parking)
5. [Réserver une place](#réserver-une-place)
6. [Check-in](#check-in)
7. [Annuler une réservation](#annuler-une-réservation)
8. [Statuts de réservation](#statuts-de-réservation)
9. [Codes QR](#codes-qr)
10. [FAQ](#faq)

---

## Présentation

**SA-Arasaka** est une application web de gestion de réservations de places de parking. Elle permet aux employés de réserver une place, de s'y enregistrer via un QR code, et aux secrétaires de gérer l'ensemble des réservations actives.

**Parking disponible :**
- 60 places au total, réparties sur 6 rangées (A à F)
- 10 places par rangée (01 à 10)
- Les rangées **A** et **F** sont équipées de bornes de recharge électrique

---

## Connexion

Accédez à l'application via votre navigateur et connectez-vous avec vos identifiants professionnels.

| Champ | Description |
|-------|-------------|
| **Email** | Votre adresse email professionnelle |
| **Mot de passe** | Votre mot de passe |

Après connexion, vous êtes redirigé vers le tableau de bord parking. Votre session est valide pendant **24 heures**.

> **Comptes de test disponibles :**
>
> | Email | Rôle | Mot de passe |
> |-------|------|-------------|
> | `employee@company.com` | Employé | `password123` |
> | `manager@company.com` | Manager | `password123` |
> | `secretary@company.com` | Secrétaire | `password123` |

---

## Rôles et permissions

### Employé (`EMPLOYEE`)

- Réserver une place pour une période allant jusqu'à **5 jours**
- Effectuer son check-in (manuellement ou par QR code)
- Annuler ses propres réservations en statut **PENDING**
- Consulter son historique de réservations

### Manager (`MANAGER`)

- Réserver une place pour une période allant jusqu'à **30 jours**
- Effectuer son check-in
- Annuler ses propres réservations en statut **PENDING**
- Consulter son historique de réservations

### Secrétaire (`SECRETARY`)

- Voir **toutes** les réservations actives
- Annuler **n'importe quelle** réservation en statut **PENDING**
- Toutes les fonctionnalités d'un employé

---

## Tableau de bord parking

Le tableau de bord affiche la carte complète du parking avec toutes les places, organisées par rangée.

### Légende des couleurs

| Couleur | Signification |
|---------|---------------|
| **Vert** | Place disponible (après une recherche) |
| **Gris** | Place non disponible ou non recherchée |
| **Bleu** | Place sélectionnée (en cours de réservation) |
| **Orange** | Place avec une réservation en attente (**PENDING**) |
| **Violet** | Place avec une réservation confirmée (**CONFIRMED**) |

### Indicateur de recharge électrique

Les places dotées d'une borne de recharge affichent un indicateur ⚡. Ce sont les places des rangées **A** et **F**.

---

## Réserver une place

### Étape 1 — Rechercher les disponibilités

1. Renseigner la **date de début** et la **date de fin** souhaitées.
2. Cocher **Recharge électrique** si votre véhicule en a besoin.
3. Cliquer sur **Rechercher**.

Les places disponibles pour cette période apparaissent en vert sur la carte.

> **Contraintes de durée :**
> - Employé : maximum **5 jours**
> - Manager : maximum **30 jours**

### Étape 2 — Sélectionner une place

Cliquer sur une place **verte** pour la sélectionner. Elle passe en bleu.

Pour changer de sélection, cliquer sur une autre place verte.

### Étape 3 — Confirmer la réservation

Cliquer sur **Confirmer la réservation**. La réservation est créée avec le statut **PENDING**.

> Si vous avez déjà une réservation qui chevauche les dates sélectionnées, la réservation sera refusée.

---

## Check-in

Le check-in valide votre présence sur la place réservée. Il doit être effectué **le jour de début** de votre réservation, **avant 11h00**.

> **Important :** Les réservations non confirmées avant 11h00 (jours ouvrés) sont automatiquement **expirées**.

### Méthode 1 — Via QR code (recommandée)

1. Repérer le QR code affiché sur votre place de parking.
2. Scanner le QR code avec votre smartphone.
3. L'application s'ouvre sur la page de check-in.
4. Si votre réservation est valide, elle est automatiquement confirmée.

### Méthode 2 — Via l'URL directe

Accéder à l'adresse :
```
http://<url-application>/checkin/<LABEL>
```
Où `<LABEL>` est l'identifiant de la place (ex. `A01`, `F10`).

---

## Annuler une réservation

1. Accéder à la section **Mes réservations** dans le tableau de bord.
2. Trouver la réservation à annuler (statut **PENDING**).
3. Cliquer sur **Annuler**.

> **Règles d'annulation :**
> - Seules les réservations avec le statut **PENDING** peuvent être annulées.
> - Une réservation **CONFIRMED** ne peut pas être annulée.
> - Les secrétaires peuvent annuler les réservations de tous les utilisateurs.

---

## Statuts de réservation

| Statut | Description |
|--------|-------------|
| **PENDING** | Réservation créée, en attente de check-in |
| **CONFIRMED** | Check-in effectué — présence validée |
| **CANCELLED** | Réservation annulée manuellement |
| **EXPIRED** | Check-in non effectué avant 11h00 — réservation expirée automatiquement |

### Cycle de vie d'une réservation

```
Création
   │
   ▼
PENDING ──────────────────► CANCELLED  (annulation manuelle)
   │
   ├─── Avant 11h00 ──────► CONFIRMED  (check-in effectué)
   │
   └─── Après 11h00 ──────► EXPIRED    (expiration automatique)
```

---

## Codes QR

### Pour les administrateurs / secrétaires

Une page de génération de QR codes est disponible à :
```
http://<url-application>/debug/qrcodes
```

Cette page affiche un QR code pour chaque place de parking, prêt à être imprimé et apposé sur les panneaux de stationnement.

- Les QR codes sont organisés par rangée.
- Les places avec recharge électrique sont identifiées par un indicateur ⚡.
- Un bouton **Imprimer** permet d'imprimer directement la page.

> Chaque QR code encode l'URL `/checkin/<LABEL>` de la place correspondante.

---

## FAQ

### Je ne peux pas réserver, pourquoi ?

- **Chevauchement de dates :** Vous avez déjà une réservation sur cette période. Consultez vos réservations actives.
- **Place indisponible :** La place est déjà réservée sur cette période. Relancez une recherche pour voir les places libres.
- **Durée dépassée :** Votre rôle limite la durée de réservation (5 jours pour un employé, 30 jours pour un manager).
- **Date passée :** Il n'est pas possible de réserver pour une date passée.

### Mon check-in a échoué, pourquoi ?

- **Pas de réservation pour aujourd'hui :** Le check-in n'est valide que le jour du début de la réservation.
- **Réservation déjà expirée :** Si 11h00 est passée et que le check-in n'a pas été effectué, la réservation a expiré.
- **Réservation déjà confirmée :** Vous avez déjà effectué votre check-in.

### J'ai oublié de faire mon check-in avant 11h00

La réservation est automatiquement expirée. Vous devez créer une nouvelle réservation.

### Peut-on avoir plusieurs réservations actives simultanément ?

Non. Vous ne pouvez pas avoir deux réservations dont les périodes se chevauchent.

### Que se passe-t-il le week-end ?

L'expiration automatique des réservations à 11h00 ne s'applique qu'aux **jours ouvrés** (lundi au vendredi). Les réservations du week-end ne sont pas expirées automatiquement.

### Comment savoir si ma place est équipée d'une borne électrique ?

Les places équipées d'une borne de recharge sont dans les rangées **A** et **F**. Elles sont identifiées par un indicateur ⚡ dans l'interface.

---

*SA-Arasaka — Système de gestion de parking*
