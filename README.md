## 📋 HashWorld v0.0.1 - Guide d'utilisation

### Description de la librairie
Cette librairie permet de gérer les mondes facilement, à l'aide d'un monde de template.

## 🏁 Getting Started

### Informations

HashWorld est à la fois un plugin et une librairie. 
Pour l'utiliser, il faudra donc placer le `.jar` dans le dossier `/plugins`.

### Utilisation
```java
import fr.hashtek.spigot.hashworld.HashWorld;
import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin
{

    @Override
    public void onEnable()
    {
        HashWorld HWInstance = HashWorld.getInstance();
        
        # Recharger le premier clone
        HWInstance.reloadClone(0);
        
        # Obtenir le premier clone
        World firstClone = HWInstance.getClone(0);
    }

}
```

### Fichier de configuration
<u>config.yml</u>:
```yaml
worlds:
  template:
    name: "template"
  clones:
    format-name: "clone-%id%" # %id% => the id of the world
    amount: 5
```
`worlds.template.name`: Le nom du fichier de la template (présent à la racine du serveur).
`clones.format-name`: Le format d'affichage des clones.
`clones.amount`: Le nombre de clones à charger.

> [!information]
> `clones.format-name`:
> La variable `%id%` correspond à l'identifiant du clone.
> Si cette variable n'est pas indiquée, alors le format sera `<format-name>-%id%`

