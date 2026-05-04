# EScore

Aplicação móvel Android para gestão de eventos desportivos, permitindo criar torneios, gerir equipas e jogadores, calendarizar jogos e acompanhar resultados em tempo real.

---

##  Funcionalidades

- Criação e gestão de torneios (liga, grupos, eliminatórias)
- Gestão de equipas e jogadores
- Calendarização de jogos
- Registo de eventos (golos, faltas, etc.)
- Classificações e estatísticas automáticas
- Notificações em tempo real
- Modo offline com sincronização automática

---

##  Perfis de Utilizador

- **Administrador** – controlo total da plataforma  
- **Organizador** – gere torneios, equipas e jogos  
- **Jogador/Utilizador** – consulta eventos, resultados e estatísticas  

---

##  Modelo de Dados

O sistema é baseado nas seguintes entidades principais:

- **Utilizador** (perfis e autenticação)
- **Torneio** (informação da competição)
- **Equipa** (equipas participantes)
- **Jogador** (dados individuais)
- **Jogo** (partidas e resultados)
- **EventoJogo** (golos, faltas, etc.)
- **Classificação** (ranking das equipas)
- **Estatísticas** (individuais e coletivas)
- **Notificação** (alertas do sistema)
- **SincronizaçãoOffline** (gestão de dados offline)

---

##  Arquitetura

A aplicação segue uma estrutura modular com separação entre:

- Interface (UI/UX)
- Lógica de negócio
- Persistência de dados

Inclui suporte a funcionamento offline com sincronização automática quando a ligação é restabelecida.

---

##  Objetivo

Centralizar toda a gestão de eventos desportivos numa única plataforma, oferecendo uma experiência simples, intuitiva e eficiente para organizadores, jogadores e espectadores.

---
