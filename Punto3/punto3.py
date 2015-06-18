from nltk import load_parser
parser = load_parser('G2.fcfg',trace=0)
f = open('Sentences.txt')
lines = f.readlines()
out = open('G2.txt','w')
f.close()
for line in lines:
    tokens = line.split()
    trees = parser.nbest_parse(tokens)
    for tree in trees:
        print tree.node['SEM']
        out.write(str(tree.node['SEM'])+'\n')
out.close()
