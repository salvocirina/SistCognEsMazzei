from nltk import load_parser
parser = load_parser('G2.fcfg',trace=0)
f = open('Sentences.txt')
lines = f.readlines()
out = open('G2.txt','w')
f.close()
for line in lines:
    tokens = line.split()
    trees = parser.parse(tokens)
    for tree in trees:
        print tree.label()['SEM']
        out.write(str(tree.label()['SEM'])+'\n')
out.close()
