# information-retrieval-design

# projet-recherche-d-information
le projet du moteure de recherche première partie ( la creation du fichier invererse ) 
Implémentation d’un SRI basé sur un fichier inverse riche en considérant dans le posting le stockage des
informations: id du document qui contient le terme, le poids du terme et les balises où le terme apparait. La
normalisation repose sur l’emploi de la succession des règles de transformation ci-dessous.
  1. Si (suffixe= sses)alors le remplacer pares ;
  2. Si (suffixe= ies)alors le remplacer pari ;
  3. Si (suffixe= s)alors le supprimer ;
  4. Si (m>0 et suffixe= ed) alors le supprimer ;
  5. Si (m >0 et suffixe= ing) alors le supprimer ;
  6. Si (suffixe= y) alors le remplacer par i ;
  7. Si (m>0et suffixe= ational) alors le remplacer parate ;
  8. Si (m>0et suffixe= tional) alors le remplacer partion ;
  9. Si (m>0et suffixe= izer) alors le remplacer parize ;
  10. Si (m>0et suffixe= alize) alors le remplacer paral ;
  11. Si (m> 1et suffixe= ize) alors le supprimer ;
Avec m est la mesure de séquences (voyelles-consonnes) dans le préfixe du mot obtenu après application d’une
règle.
Le poids d’un terme t dans un document D est calculé par : poids(t,D)= poids(t, title)+poids(t, abstract)
Avec :
  - poids(t, title) est le poids du terme t dans la balise TITLE, mesuré par la fréquence tu terme dans le TITLE ;
  - poids(t, abstract) est le poids du terme t dans l’ABSTRACT, mesuré par la formule :

  poids (t,Abstract) = idf(t) * freq (t,Abstract) / |abstract| 
